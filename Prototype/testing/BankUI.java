import com.thinking.machines.nframework.client.*;
class BankUI
{
public static void main(String gg[])
{
try
{
NFrameWorkClient client=new NFrameWorkClient( );
String branchName=(String)client.execute("/banking/branchName",gg[0]);
System.out.println(branchName);
}
catch(Throwable t)
{
System.out.println("Type : "+t.getClass( ).getName()+" -> "+t.getMessage( ));
}
}
};