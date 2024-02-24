package com.thinking.machines.nframework.server;
import java.util.*;
import java.lang.reflect.*;
import java.net.*;
import com.thinking.machines.nframework.server.annotations.*;
public class NFrameWorkServer
{
private Set<Class> tcpNetworkServiceClasses;
private Map<String,TCPService> services;
public NFrameWorkServer()
{
this.tcpNetworkServiceClasses=new HashSet<>();
this.services=new HashMap<>();
}
public void registerClass(Class c)
{
TCPService tcpService=null;
Path pathOnType;
Path pathOnMethod;
Method methods[ ];
String fullPath;
pathOnType=(Path)c.getAnnotation(Path.class);
if(pathOnType==null)return;
methods=c.getMethods( );
int servicePathCount=0;
for(Method method : methods)
{
pathOnMethod=(Path)method.getAnnotation(Path.class);
if(pathOnMethod==null)continue;
servicePathCount++;
fullPath=pathOnType.value()+pathOnMethod.value();
tcpService=new TCPService( );
tcpService.c=c;
tcpService.method=method;
tcpService.path=fullPath;
this.services.put(fullPath,tcpService);
}
if(servicePathCount>0)this.tcpNetworkServiceClasses.add(c);
}
public TCPService getTCPService(String path)
{
if(this.services.containsKey(path))return this.services.get(path);
else return null;
}
public void start( )
{
try
{
ServerSocket serverSocket=new ServerSocket(5500);
Socket socket;
RequestProcessor requestProcessor;
while(true)
{
socket=serverSocket.accept( );
requestProcessor=new RequestProcessor(this,socket);
}
}
catch(Exception e)
{

}
}
};