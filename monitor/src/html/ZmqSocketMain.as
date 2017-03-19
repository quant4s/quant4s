package
{
	import flash.events.*;
	import flash.display.Sprite;
	import flash.external.*;
	import flash.utils.*;
	import ZmqSocket;

	public class ZmqSocketMain extends Sprite
	{
		private var sockets: Array = new Array;
		
		public function ZmqSocketMain ()
		{
			super ();
			
			if (!ExternalInterface.available) 
				return;
			
			ExternalInterface.addCallback ("create", create);
			ExternalInterface.addCallback ("connect", connect);
			ExternalInterface.addCallback ("close", close);
			ExternalInterface.addCallback ("available", available);
			ExternalInterface.addCallback ("recv", recv);
			ExternalInterface.addCallback ("send", send);
			if (!isJavaScriptReady ())
			{
				var timer: Timer = new Timer (100, 0);
				timer.addEventListener (TimerEvent.TIMER, function (event: TimerEvent)
					{
						if (isJavaScriptReady ())
						{
			                Timer (event.target).stop();
							confirmFlashReady ();
						}
					});
				timer.start ();
			}
			else
			{
				confirmFlashReady ();
			}
		}
		
		private function isJavaScriptReady (): Boolean
		{
			try
			{
				return ExternalInterface.call ("ZmqSocket.__isJSReady");
			}
			catch (e: Error)
			{
			}
			return false;
		}
		
		private function confirmFlashReady (): void
		{
			ExternalInterface.call ("ZmqSocket.__onFlashReady");
		}
		
		private function create (id: uint, identity: String): void
		{
			sockets[id] = new ZmqSocket (identity);

			sockets[id].addEventListener (ZmqSocket.OPEN, function (e: Event) { onOpen (id); });
			sockets[id].addEventListener (ZmqSocket.MESSAGE, function (e: Event) { onMessage (id); });

			//  These are propagated upwards.
			sockets[id].addEventListener (Event.CLOSE, function (e: Event) { onClose (id); });
			sockets[id].addEventListener (SecurityErrorEvent.SECURITY_ERROR, 
				function (e: SecurityErrorEvent) { onError (id, e); });
			sockets[id].addEventListener (IOErrorEvent.IO_ERROR, 
				function (e: IOErrorEvent) { onError (id, e); });
		}
		
		private function connect (id: uint, host: String, port: uint): void
		{
			sockets[id].connect (host, port);
		}
		
		private function close (id: uint): void
		{
			sockets[id].close ();
		}
		
		private function available (id: uint): Number
		{
			return sockets[id].available ();
		}
		
		private function send (id: uint, msg: Array): void
		{
			sockets[id].send (msg);
		}
		
		private function recv (id: uint): Array
		{
			return sockets[id].recv ();
		}
		
		private function onOpen (id: uint): void
		{
			ExternalInterface.call ("ZmqSocket.__onopen", id);
		}

		private function onClose (id: uint): void
		{
			ExternalInterface.call ("ZmqSocket.__onclose", id);
		}

		private function onMessage (id: uint): void
		{
			ExternalInterface.call ("ZmqSocket.__onmessage", id);
		}

		private function onError (id: uint, e: Event): void
		{
			ExternalInterface.call ("ZmqSocket.__onerror", id, e.toString ());
		}
	}
}
