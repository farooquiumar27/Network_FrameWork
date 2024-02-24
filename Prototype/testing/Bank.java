import com.thinking.machines.nframework.server.*;
import com.thinking.machines.nframework.server.annotations.*;
@Path("/banking")
public class Bank
{
@Path("/branchName")
public String getBranchName(String city) throws BankingException
{
System.out.println("Method got called");
if(city.equals("Ujjain"))
{
return "Freegang";
}
else if(city.equals("Bombay"))
{
return "Colaba";
}
throw new BankingException("No such branch in that city");
}
public static void main(String gg[ ])
{
NFrameWorkServer server=new NFrameWorkServer( );
server.registerClass(Bank.class);
server.start( );
}
};