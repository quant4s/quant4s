(function ()
{
 // Check if module is already initialized.
 if (window.ZmqSocket)
  return;
 
 // Constructor. You may pass socket identity here.
 ZmqSocket = function (identity)
 {
  this.fd = ZmqSocket.__nextFd++;
  ZmqSocket.__sockets[this.fd] = this;

  this.identity = identity;
  this.state = ZmqSocket.CONNECTING;
  this.onopen = function () {};
  this.onmessage = function () {};
  this.onerror = function (msg) {};
  this.onclose = function () {};

  var athis = this;
  ZmqSocket.__addTask (function ()
   {
    ZmqSocket.__flash.create (athis.fd, athis.identity);
   });
 };

 ZmqSocket.__sockets = [];
 ZmqSocket.__tasks = [];
 ZmqSocket.__nextFd = 0;
 ZmqSocket.__flash = null;
 
 ZmqSocket.CONNECTING = 1;
 ZmqSocket.OPEN = 2;
 ZmqSocket.CLOSING = 3;

 // Called by Flash when it's ready.
 ZmqSocket.__onFlashReady = function ()
 {
  if (navigator.appName.indexOf("Microsoft") != -1)
   ZmqSocket.__flash = window["ZmqSocketFlash"];
  else
   ZmqSocket.__flash = document["ZmqSocketFlash"];

  // Make it later to avoid recursion.
  ZmqSocket.__later (function ()
   {
    for (var i = 0; i < ZmqSocket.__tasks.length; i++)
     ZmqSocket.__tasks[i]();
   });
 };
 
 // Used to make calls in a separate event handler to avoid
 // recursive calls to Flash - recursion is not supported by many browsers.
 ZmqSocket.__later = function (task)
 {
  var to = setTimeout (function () { clearTimeout (to); task (); }, 0);
 }
 
 // Called by Flash to find out if JS is ready.
 ZmqSocket.__isJSReady = function () { return true; };
 
 // Called when we are not sure that flash is ready.
 ZmqSocket.__addTask = function (task)
 {
  if (ZmqSocket.__flash)
   task ();
  else
   ZmqSocket.__tasks.push (task);
 }
 
 ZmqSocket.prototype.connect = function (host, port)
 {
  var athis = this;
  ZmqSocket.__addTask (function ()
   {
    ZmqSocket.__flash.connect (athis.fd, host, port);
   }); 
 }

 ZmqSocket.prototype.close = function ()
 {
  this.state = ZmqSocket.CLOSING;
  var athis = this;
  ZmqSocket.__addTask (function ()
   {
    alert('before');
    ZmqSocket.__flash.close (athis.fd);
    alert('after');
   }); 
 }

 ZmqSocket.prototype.available = function ()
 {
  if (this.state != ZmqSocket.OPEN)
   throw "Invalid state, socket must be connected!";

  return ZmqSocket.__flash.available (this.fd);
 }

 ZmqSocket.prototype.recv = function ()
 {
  if (this.state != ZmqSocket.OPEN)
   throw "Invalid state, socket must be connected!";

  return ZmqSocket.__flash.recv (this.fd);
 }

 ZmqSocket.prototype.send = function (msg)
 {
  if (this.state != ZmqSocket.OPEN)
   throw "Invalid state, socket must be connected!";

  return ZmqSocket.__flash.send (this.fd, msg);
 }

 ZmqSocket.__onopen = function (fd)
 {
  // Avoiding recursion
  ZmqSocket.__later (function ()
   {
    ZmqSocket.__sockets[fd].state = ZmqSocket.OPEN;
    ZmqSocket.__sockets[fd].onopen ();
   });
 }

 ZmqSocket.__onmessage = function (fd)
 {
  // Avoiding recursion
  ZmqSocket.__later (function () { ZmqSocket.__sockets[fd].onmessage (); });
 }

 ZmqSocket.__onerror = function (fd, msg)
 {
  // Avoiding recursion
  ZmqSocket.__later (function () { ZmqSocket.__sockets[fd].onerror (msg); });
 }

 ZmqSocket.__onclose = function (fd)
 {
  // Avoiding recursion
  ZmqSocket.__later (function () { ZmqSocket.__sockets[fd].onclose (); });
 }

})();