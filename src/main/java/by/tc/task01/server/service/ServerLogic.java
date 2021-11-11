package by.tc.task01.server.service;

import by.tc.task01.server.entity.StudentInfo;
import by.tc.task01.server.entity.criteria.ClientCriteria;
import by.tc.task01.server.entity.criteria.SearchCriteria;
import by.tc.task01.server.serverconsole.CommandReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServerLogic {
    private ClientCriteria clientCriteria;
    private Server server;
    private boolean work;
    private List<Thread> threads;
    private List<StudentInfo> students;
    public ServerLogic(){
        clientCriteria = new ClientCriteria(SearchCriteria.Client.getCriteriaName());
        threads = new ArrayList<Thread>();
        server = new Server(this);
        Thread consoleReader = new CommandReader(this);
        consoleReader.start();
    }

    public void startConnection() throws InterruptedException, IOException {
        boolean isConnect = false;
        while (!isConnect){
            isConnect = server.makeConnection();
        }
        work = true;
        while (work){
            String command = server.getCommand();
            Thread newCommand = new CommandHandler(command, this, clientCriteria);
            newCommand.start();
            if (!command.equals("EXIT")){
                threads.add(newCommand);
            }
        }
        server.close();
        System.out.println("STOPPED");
    }

    public void stopConnection() throws InterruptedException, IOException {
        for (Thread thread: threads){
            thread.join();
        }
        work = false;
        server.sendClose();
    }

    public void sendData(String data) throws IOException, InterruptedException {
        server.sendData(data);
    }
}
