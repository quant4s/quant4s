/*
    Copyright (c) 2011 by Artur Brugeman
	License: GNU GPL v3
*/

package 
{

	import flash.errors.*;
	import flash.events.*;
	import flash.net.Socket;
	import flash.utils.ByteArray;
    import flash.system.*;

	public class ZmqSocket extends EventDispatcher 
	{

		//  Encapsulated tcp socket.
		private var socket: Socket = null;
		//  Identity of socket, null if anonymous.
		private var identity: String = null;
		//  Identity of peer, null if anonymous.
		private var peerIdentity: String = null;
		//  True if received peer's identity.
		private var connected: Boolean = false;
		//  FIFO queue of received messages 
		private var queue: Array = null; 
		//  States that there are more frames of last message 
		private var more: Boolean = false;
		//  Current message, stored here until all frames are read
		private var current: Array = null;
		//  Number of bytes required for current decoder
		//  1 is value required by frameLengthDecoder
		private var decoderMinBytes: int = 1;
		//  When decoderMinBytes are available, they are read
		//  and decoder is called. The decoder returns pointer
		//  to next decoder, and modifies decoderMinBytes accordingly.
		private var decoder: Function = frameLengthDecoder;
		//  Length of current frame
		private var frameLength: uint = 0;
		//  Flags of current frame
		private var frameFlags: uint = 0;
	
		//  Dispatched events

		//  Dispatched when peer's identity is received
		public static var OPEN: String = "zmq_socket_open";
		//  Dispatched when a message is received
		public static var MESSAGE: String = "zmq_socket_message";
		//  Other dispatched events are CLOSE, SECURITY_ERROR and IO_ERROR
	
		//  Constructor might throw a security exception.
		public function ZmqSocket (identity: String = null)
		{
			super ();
			// FIXME identity must be < 256 bytes
			this.identity = identity;
			queue = new Array;

			socket = new Socket;
			Security.allowDomain("localhost");
			//  These are handled to send identity and read messages.
			socket.addEventListener (Event.CONNECT, connectHandler);
			socket.addEventListener (ProgressEvent.SOCKET_DATA, socketDataHandler);

			//  These are propagated upwards.
			socket.addEventListener (Event.CLOSE, 
									 function (e: Event) { dispatchEvent (e.clone ()); });
			socket.addEventListener (SecurityErrorEvent.SECURITY_ERROR, 
									 function (e: SecurityErrorEvent) { dispatchEvent (e.clone ()); });
			socket.addEventListener (IOErrorEvent.IO_ERROR, 
									 function (e: IOErrorEvent) { dispatchEvent (e.clone ()); });
		}
		
		//  Add event listeners before connecting.
		public function connect (host: String, port: int)
		{
			socket.connect (host, port);
		}
		
		//  Call to close TCP connection.
		public function close ()
		{
			socket.close ();
		}
		
		//  Checks whether there are messages available for reading.
		public function available (): Boolean
		{
			return queue.length > 0;
		}
		
		//  Read a message consisting of String-s, returns null if no messages available.
		//  Doesn't block.
		public function recv (): Array
		{
			if (queue.length > 0)
			{
				var bin: Array = queue.shift ();
				var str: Array = new Array;
				while (bin.length > 0)
				{
					var frame: ByteArray = bin.shift ();
					if (frame != null)
						str.push (frame.readUTFBytes (frame.length));
					else
						str.push ("");
				}
				return str;
			}
			return null;
		}
		
		//  Read a message consisting of ByteArray-s, returns null if no 
		//  messages available. Doesn't block.
		public function recvBin (): Array
		{
			if (queue.length > 0)
				return queue.shift ();
			return null;
		}
		
		//  Send a message consisting of String-s, triggets IO_ERROR event if 
		//  socket is not connected or msg == null.
		public function send (msg: Array): void
		{
			if (!canSend (msg))
				return;

			while (msg.length > 0)
			{
				var frame: String = msg.shift ();
				writeFrame (frame, msg.length > 0);
			}
		}
		
		//  Send a message consisting of ByteArray-s, triggets IO_ERROR event if 
		//  socket is not connected or msg == null.
		public function sendBin (msg: Array): void
		{
			if (!canSend (msg))
				return;

			while (msg.length > 0)
			{
				var frame: ByteArray = msg.shift ();
				writeFrameBin (frame, msg.length > 0);
			}
		}

		private function canSend (msg: Array): Boolean
		{
			if (!connected)
			{
				dispatchEvent (new IOErrorEvent (IOErrorEvent.IO_ERROR, false, false, 
												 "Socket is not connected yet"));
				return false;
			}
			if (msg == null)
			{
				dispatchEvent (new IOErrorEvent (IOErrorEvent.IO_ERROR, false, false, 
												 "Input message is NULL"));
				return false;
			}
			return true;
		}

		private function writeFrame (frame: String, more: Boolean = false): void
		{
			var buf: ByteArray = new ByteArray;
			if (frame != null && frame.length > 0)
				buf.writeUTFBytes (frame);

			writeFrameBin (buf, more);
		}
		
		private function writeFrameBin (frame: ByteArray, more: Boolean = false): void
		{
			//  Length
			var len: int = frame.length + 1;
			if (len < 255)
			{
				socket.writeByte (len);
			}
			else
			{
				//  Flags 64-bit length 
				socket.writeByte (0xFF);
				//  Leading 32 bits set to 0
				socket.writeUnsignedInt (0);
				//  Least significant 32 bits are our length
				socket.writeUnsignedInt (len);
			}

			//  Flags
			if (more)
				socket.writeByte (1);
			else
				socket.writeByte (0);

			//  Data
			if (frame.length > 0)
				socket.writeBytes (frame);
		}
	
		private function readNextFrame (): Boolean
		{
			//  Main decoding cycle 
			while (socket.bytesAvailable >= decoderMinBytes)
			{
				//  Read bytes required by current decoder
				var bytes: ByteArray = new ByteArray;
				socket.readBytes (bytes, 0, decoderMinBytes);
				
				//  Decode.
				decoder = decoder (bytes);
	
				//  Returned NULL means a new frame was read.
				if (decoder == null)
				{
					
					//  Set initial decoder to read the next frame.
					decoderMinBytes = 1;
					decoder = frameLengthDecoder;
					
					//  True means frame was read.
					return true;
				}
			}
			
			//  No frame was read.
			return false;
		}
	
		private function frameLengthDecoder (bytes: ByteArray): Function
		{
			//  First byte is length.
			frameLength = bytes.readUnsignedByte ();

			//  Length == 0 is bad
			if (frameLength == 0)
			{
				dispatchEvent (new IOErrorEvent (IOErrorEvent.IO_ERROR, false, false, 
												 "Socket is sending bad length"));
				socket.close ();

				return null;
			}
				
			//  Indicates 64-bit length
			if (frameLength == 0xFF)
			{
				decoderMinBytes = 8;
				return frameBigLengthDecoder;
			}
			
			return frameFlagsDecoder;
		}
		
		private function frameBigLengthDecoder (bytes: ByteArray): Function
		{
			frameLength = bytes.readUnsignedInt ();

			//  If significant 32-bits are not 0, means message
			//  size exceeds maximum, and we close connection.
			if (frameLength != 0)
			{
				dispatchEvent (new IOErrorEvent (IOErrorEvent.IO_ERROR, false, false, 
												 "Incoming message size is too big"));

				socket.close ();
				return null;
			}
			
			//  Least significant 32 bits are the length.
			frameLength = bytes.readUnsignedInt ();
			if (frameLength == 0)
			{
				dispatchEvent (new IOErrorEvent (IOErrorEvent.IO_ERROR, false, false, 
												 "Socket is sending bad length"));
				socket.close ();

				return null;
			}
	
			decoderMinBytes = 1;
			return frameFlagsDecoder;
		}
		
		private function frameFlagsDecoder (bytes: ByteArray): Function
		{
			frameFlags = bytes.readUnsignedByte ();
			more = (frameFlags & 0x1) == 0x1;
			
			//  Body length is 1 byte less than frame length (flags take 1 byte).
			decoderMinBytes = frameLength - 1;

			//  If message contained only flags, we're done
			if (frameLength <= 1) 
			{
				if (current == null)
					current = new Array;
				current.push (null);
				
				//  If it's the last frame of message, push current message to queue.
				if (!more)
				{
					queue.push (current);
					current = null;
					return null;
				}
				else
				{
					
					//  Reset decoder to read next frame.
					decoderMinBytes = 1;
					return frameLengthDecoder;
				}
			}
			
			return frameBodyDecoder;
		}
		
		private function frameBodyDecoder (bytes: ByteArray): Function
		{
			if (current == null)
				current = new Array;
			current.push (bytes);
			
			
			//  If it's the last frame of message, push current message to queue.
			if (!more)
			{
				queue.push (current);
				current = null;
				return null;
			}
			else
			{

				//  Reset decoder to read next frame.
				decoderMinBytes = 1;
				return frameLengthDecoder;
			}
		}
	
		private function connectHandler (event: Event): void 
		{
			//  Reset decoder.
			decoderMinBytes = 1;
			decoder = frameLengthDecoder;

			//  Now we must send greeting
			writeFrame (identity);
		}
	
		private function socketDataHandler (event: ProgressEvent): void 
		{
			//  If frame hasn't arrived yet, just wait for more data.
			if (!readNextFrame ())
				return;
	
			//  If we haven't got peer's identity yet, read it now.
			if (!connected)
			{

				//  If peer says that identity has more than 1 frame, than he's bad.
				if (more)
				{
					dispatchEvent (new IOErrorEvent (IOErrorEvent.IO_ERROR, false, false, 
													 "Peer has malformed identity"));
					socket.close ();
					return;
				}
				else
				{

					//  Accept peer's identity
					current = queue.shift () as Array;
					if (current.length > 0)
						peerIdentity = current.shift ();
					connected = true;
					dispatchEvent (new Event (OPEN));
				}
			}
			else
			{
				if (!more)
					dispatchEvent (new Event (MESSAGE));
			}
		}
	}
}