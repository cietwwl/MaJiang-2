package filter;
public interface IWorker {  
  
    /** 
     * ������ 
     * @param iphone 
     * @author lifh 
     */  
    void handleIphone(Iphone iphone);  
    /** 
     * ������һ�������� 
     * @param worker 
     * @author lifh 
     */  
    void setNext(IWorker worker);  
}  