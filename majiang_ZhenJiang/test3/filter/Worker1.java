package filter;
public class Worker1 implements IWorker {  
  
    private IWorker next;  
  
    public void handleIphone(Iphone iphone) {  
        iphone.setState(iphone.getState() + "�ұ�װ��һ����ɫ�ĺ�ǣ�");  
        if (next != null)  
            next.handleIphone(iphone);  
    }  
  
    public void setNext(IWorker worker) {  
        this.next = worker;  
  
    }  
  
}  