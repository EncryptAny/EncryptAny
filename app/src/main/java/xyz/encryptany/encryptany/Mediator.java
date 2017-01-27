package xyz.encryptany.encryptany;

/**
 * Created by dakfu on 1/26/2017.
 */

public class Mediator {

    AppAdapterInterface appAdapter;
    UIAdapterInterface uiAdapter;

    public Mediator(){

    }

    public boolean getMessages(){
        return false;
    }

    public boolean sendMessage(){
        return false;

    }

    private boolean encryptMessage(){
        return false;
    }

    private boolean archiveMessage(){
        return false;
    }

    private boolean displayReceivedMessage(){
        return false;
    }

    private boolean displaySentMessage(){
        return false;
    }

    private boolean receiveMessageFromApp(){
        return false;
    }

    private boolean sendMessageToApp(){
        return false;
    }





}
