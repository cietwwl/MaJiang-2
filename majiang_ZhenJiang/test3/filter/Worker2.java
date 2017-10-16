package filter;
public class Worker2 implements IWorker {  
  
    private IWorker next;  
  
    public void handleIphone(Iphone iphone) {  
        iphone.setState(iphone.getState() + "我被装了一块电池；");  
        if (next != null)  
            next.handleIphone(iphone);  
    }  
  
    public void setNext(IWorker worker) {  
        this.next = worker;  
    }  
}  