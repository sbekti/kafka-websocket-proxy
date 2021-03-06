# anubis-server
Kafka proxy server over WebSocket.

## Prerequisites
Make sure that you use Kafka 0.10!

## Installation
Clone the repository:
~~~shell
git clone https://github.com/sbekti/anubis-server.git
cd anubis-server
~~~
Compile the project:
~~~shell
gradle build
~~~
Edit the config file as necessary:
~~~shell
vim conf/config.properties
~~~
Run the server:
~~~shell
bin/start.sh
~~~

## Protocol

Send these JSONs over the WebSocket connection to perform the corresponding commands.

### Subscribing
~~~javascript
{
  "event": "subscribe",
  "topics": ["fruits", "cities"],
  "groupId": "testgroup"
}
~~~
After subscribing, you will get the messages automatically over the WebSocket connection.

### Publishing
~~~javascript
{
  "event": "publish",
  "topic": "fruits",
  "value": "apple"
}
~~~

### Committing Records
~~~javascript
{
  "event": "commit",
  "topic": "fruits",
  "partitionId": 0
  "offset": 3
}
~~~

### Seeking
~~~javascript
{
  "event": "seek",
  "topic": "fruits",
  "value": "beginning" // or "end" or 3
}
~~~

### Unsubscribing
~~~javascript
{
  "event": "unsubscribe"
}
~~~

## License

(The MIT License)

Copyright (c) 2016 Samudra Harapan Bekti <samudra.bekti@gmail.com>

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
'Software'), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
