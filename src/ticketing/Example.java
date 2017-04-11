package ticketing;

import java.util.*;

import ticketing.User.UserClass;

public class Example {

    public static void main(String[] args) throws TicketException {
        
        IssueManager ts = new IssueManager();

        HashSet<UserClass> both = new HashSet<>();
        both.add(UserClass.Reporter);
        both.add(UserClass.Maintainer);
        ts.createUser("alpha", UserClass.Reporter);
        ts.createUser("beta", UserClass.Reporter);
        ts.createUser("gamma", both);
        ts.createUser("delta", both);
        ts.createUser("epsilon", UserClass.Maintainer);
        
        System.out.println(ts.getUserClasses("gamma"));
        
        ts.defineComponent("System");
        ts.defineSubComponent("SubA", "/System");
        ts.defineSubComponent("SubB", "/System");
        ts.defineSubComponent("SubC", "/System");
        ts.defineSubComponent("SubB.1", "/System/SubB");
        ts.defineSubComponent("SubB.2", "/System/SubB");
        ts.defineComponent("SystemBis");
        ts.defineSubComponent("POS", "/SystemBis");
        
        System.out.println("System has " + ts.getSubComponents("/System").size() + " children");
        System.out.println("SubB.2 has parent " + ts.getParentComponent("/System/SubB/SubB.2"));
  
        System.out.println("System has these components " + ts.getSubComponents("/System"));
        System.out.println("/System/SubB has these components " + ts.getSubComponents("/System/SubB"));
        System.out.println("/System/SubB has these components " + ts.getSubComponents("/System/SubB"));
        
        ts.openTicket("alpha", "/System/SubA", "Initial menu does not show 'open' item", Ticket.Severity.Major);
        ts.openTicket("alpha", "/System/SubA", "Cannot save form XYZ", Ticket.Severity.Major);
        ts.openTicket("alpha", "/System/SubB", "The colors in the diagram are hard to tell apart", Ticket.Severity.Minor);
        ts.openTicket("beta", "/SystemBis/POS", "Credit cards not working", Ticket.Severity.Critical);
        int id = ts.openTicket("alpha", "/System", "The system is not responding today", Ticket.Severity.Blocking);
        Ticket t = ts.getTicket(id);
        
        System.out.println("User " + t.getAuthor() + " created ticket " + t.getId() + " on component " + t.getComponent());
        
        ts.assignTicket(1, "delta");
        ts.assignTicket(2, "delta");
        ts.assignTicket(3, "epsilon");
        ts.assignTicket(4, "gamma");
        ts.assignTicket(5, "delta");
        ts.closeTicket(1, "The user had the network cable unplugged...");
        ts.closeTicket(2, "The user had the network cable unplugged...");
        ts.closeTicket(3, "The user had the network cable unplugged...");
        ts.closeTicket(4, "The user had the network cable unplugged...");
        ts.closeTicket(5, "The user had the network cable unplugged...");
        
        System.out.println("The ticket status is " + t.getState() + " solution: " + t.getDescription());
        
        System.out.println("Count open tickets:\n"  +ts.countBySeverityOfState(Ticket.State.Open));
        System.out.println("Count all tickets:\n"  +ts.countBySeverityOfState(null));
        
        
        System.out.println(ts.topMaintainers());
        
    }

}
