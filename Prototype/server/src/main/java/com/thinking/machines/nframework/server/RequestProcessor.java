package com.thinking.machines.nframework.server;
import java.net.*;
import java.io.*;
import java.nio.charset.*;
import com.thinking.machines.nframework.common.*;
import java.lang.reflect.*;
public class RequestProcessor extends Thread
{
private NFrameWorkServer server;
private Socket socket;
public RequestProcessor(NFrameWorkServer server,Socket socket)
{
this.server=server;
this.socket=socket;
start( );
}
public void start( )
{
try
{
InputStream is=socket.getInputStream( );
int bytesReadCount=0;
byte ack[ ]=new byte[1];
ack[0]=1;
byte tmp[]=new byte[1024];
byte header[]=new byte[1024];
int i=0;
int k=0;
int requestLength=1024;
int j=0;
while(j<requestLength)
{
bytesReadCount=is.read(tmp);
if(bytesReadCount==-1)continue;
for(k=0;k<bytesReadCount;k++)
{
header[i]=tmp[k];
i++;
}
j=j+bytesReadCount;
}

OutputStream os=socket.getOutputStream( );
os.write(ack,0,1);
os.flush( );

i=1023;
j=1;
int len=0;
while(i>0)
{
len=len+(header[i]*j);
i--;
j=j*10;
}

byte requestBytes[ ]=new byte[len];
i=0;
requestLength=len;
j=0;
while(j<requestLength)
{
bytesReadCount=is.read(tmp);
if(bytesReadCount==-1)continue;
for(k=0;k<bytesReadCount;k++)
{
requestBytes[i]=tmp[k];
i++;
}
j=j+bytesReadCount;
}

String requestJSONString=new String(requestBytes, StandardCharsets.UTF_8);
Request requestObject=JSONUtil.fromJSON(requestJSONString,Request.class);
String servicePath=requestObject.getServicePath( );
TCPService tcpService=this.server.getTCPService(servicePath);
Response responseObject=new Response( );
if(tcpService==null)
{
responseObject.setSuccess(false);
responseObject.setResult(null);
responseObject.setErrorMessage("Invalid path : "+servicePath);
}
else
{
Class c=tcpService.c;
Method method=tcpService.method;
try
{
Object serviceObject=c.newInstance( );
Object result=method.invoke(serviceObject,requestObject.getArguments());
responseObject.setSuccess(true);
responseObject.setResult(result);
responseObject.setErrorMessage(null);
}
catch(InstantiationException instantiationException)
{
responseObject.setSuccess(false);
responseObject.setResult(null);
responseObject.setErrorMessage("Unable to create the ovject of service class associated with path : "+servicePath);
}
catch(IllegalAccessException illegalAccessException)
{
responseObject.setSuccess(false);
responseObject.setResult(null);
responseObject.setErrorMessage("Unable to create the ovject of service class associated with path : "+servicePath);
}
catch(InvocationTargetException invocationTargetException)
{
Throwable t=invocationTargetException.getCause( );
responseObject.setSuccess(false);
responseObject.setResult(null);
responseObject.setErrorMessage(t.getMessage( ));
}
}

String responseJSONString=JSONUtil.toJSON(responseObject);
byte responseBytes[ ]=responseJSONString.getBytes( StandardCharsets.UTF_8);
int responseLength=responseBytes.length;

int x;
i=responseLength;
x=1023;
header=new byte[1024];
while(i>0)
{
header[x]=(byte)(i%10);
i=i/10;
x--;
}
os.write(header,0,1024);
os.flush( );

bytesReadCount=0;
while(true)
{
bytesReadCount=is.read(ack);
if(bytesReadCount==-1)continue;
break;
}

int chunkSize=1024;
i=0;
x=0;
while(x<responseLength)
{
if(responseLength-x<chunkSize)chunkSize=responseLength-x;
os.write(responseBytes,x,chunkSize);
os.flush( );
x=x+chunkSize;
}
bytesReadCount=0;
while(true)
{
bytesReadCount=is.read(ack);
if(bytesReadCount==-1)continue;
break;
}
socket.close( );
}
catch(IOException ioe)
{
System.out.println(ioe);
}
}
};