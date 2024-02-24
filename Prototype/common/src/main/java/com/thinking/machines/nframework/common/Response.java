package com.thinking.machines.nframework.common;
public class Response implements java.io.Serializable
{
private boolean success;
private String errorMessage;
private Object result;
public void setSuccess(boolean success)
{
this.success=success;
}
public boolean getSuccess()
{
return this.success;
}
public void setErrorMessage(String errorMessage)
{
this.errorMessage=errorMessage;
}
public String getErrorMessage()
{
return this.errorMessage;
}
public void setResult(Object result)
{
this.result=result;
}
public Object getResult()
{
return this.result;
}
public boolean hasException( )
{
return this.success==false;
}
};