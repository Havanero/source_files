package com.eurexchange.clear.frontend.manual.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Amqp {

    private static final Logger LOGGER = LoggerFactory.getLogger(Amqp.class);
    private SimpleJmsConnection jmsConnection = new SimpleJmsConnection();
    private boolean run_mode = true;
    private ArrayList<String> list_of_queues = new ArrayList<>();
    public Amqp() {
    }

    private void displayConnection() {
        boolean continueLoop = true;
        while (continueLoop) {
            System.out.println("\nSelect Connection Parameters to change or leave as default:");
            System.out.println("-----------------------------------------------------------\n");
            System.out.println(" 1) - QUEUE [" + jmsConnection.getDestinationQueue() + " ] ");
            System.out.println(" 2) - Connection [ " + jmsConnection.getConnection() + " ] \n");
            System.out.println(" 3) - Exit \n");
            System.out.print(" 0)  - to Continue:");
            Scanner enter = new Scanner(System.in);
            int selection = enter.nextInt();

            switch (selection) {
                case 0:
                    runConnection();
                    break;
                case 1:
                    System.out.print("New QUEUE: ");
                    jmsConnection.setDestinationQueue(enter.next());
                    System.out.println("QUEUE SET: " + jmsConnection.getDestinationQueue());
                    break;
                case 2:
                    System.out.print("New CONNECTION: ");
                    jmsConnection.setConnection(enter.next());
                    System.out.println("CONNECTION SET: " + jmsConnection.getDestinationQueue());
                    break;
                case 3:
                    LOGGER.info("Exiting...");
                    continueLoop = false;
                    break;
                default:
                    System.out.println("Invalid Selection");
            }

            enter.close();
        }
    }

    private void runConnection() {
        run_mode = true;
        list_of_queues.add(jmsConnection.getDestinationQueue());
        Thread t = new Thread(new Runnable() {

            @Override public void run() {
                LOGGER.info("Connected " + jmsConnection.getConnection() + "\n");
                jmsConnection.startConnection(100, list_of_queues);
                while (run_mode)
                    try {
                        Thread.sleep(3000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        run_mode = false;
                    }
            }
        });
        t.start();

    }

    public static void main(String args[]) throws IOException {
        new Amqp().displayConnection();
    }
}
