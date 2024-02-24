package com.thinking.machines.nframework.client;
import com.thinking.machines.nframework.common.*;
import com.thinking.machines.nframework.common.exceptions.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
public class NFrameWorkClient
{
public Object execute(String servicePath,Object ...arguments) throws Throwable
{
try
{
Request requestObject=new Request( );
requestObject.setServicePath(servicePath);
requestObject.setArguments(arguments);
String requestJSONString=JSONUtil.toJSON(requestObject);
byte requestBytes[ ]=requestJSONString.getBytes(StandardCharsets.UTF_8);
int requestLength=requestBytes.length;

byte header[ ]=new byte[1024];
int i,x;
i=requestLength;
x=1023;
while(i>0)
{
header[x]=(byte)(i%10);
i=i/10;
x--;
}

Socket socket=new Socket("localhost",5500);
OutputStream os=socket.getOutputStream( );
os.write(header,0,1024);
os.flush( );

InputStream is=socket.getInputStream( );
int bytesReadCount=0;
byte ack[ ]=new byte[1];
while(true)
{
bytesReadCount=is.read(ack);
if(bytesReadCount==-1)continue;
break;
}

int chunkSize=1024;
i=0;
x=0;
while(x<requestLength)
{
if(requestLength-x<chunkSize)chunkSize=requestLength-x;
os.write(requestBytes,x,chunkSize);
os.flush( );
x=x+chunkSize;
}

byte tmp[]=new byte[1024];
i=0;
int k=0;
int responseLength=1024;
int j=0;
while(j<responseLength)
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

byte responseBytes[ ]=new byte[len];
i=0;
responseLength=len;
j=0;
while(j<responseLength)
{
bytesReadCount=is.read(tmp);
if(bytesReadCount==-1)continue;
for(k=0;k<bytesReadCount;k++)
{
responseBytes[i]=tmp[k];
i++;
}
j=j+bytesReadCount;
}

os.write(ack,0,1);
os.flush( );
socket.close( );
String responseJSONString=new String(responseBytes,StandardCharsets.UTF_8);
Response responseObject=JSONUtil.fromJSON(responseJSONString,Response.class);
if(responseObject.getSuccess( ))
{
return responseObject.getResult( );
}
else
{
throw new Throwable(responseObject.getErrorMessage( ));
}
}
catch(Exception exception)
{
System.out.println(exception.getMessage());
}
return null;
}
};