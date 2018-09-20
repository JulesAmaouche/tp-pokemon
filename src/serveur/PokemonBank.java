package serveur;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import shared.Request;
import shared.Pokemon;

/**
 * This class represents the server application, which is a PokÃ©mon Bank. It is
 * a shared account: everyone's PokÃ©mons are stored together.
 *
 * @author strift
 *
 */
public class PokemonBank {

    public static final int SERVER_PORT = 3000;
    public static final String DB_FILE_NAME = "pokemons.db";

    /**
     * The database instance
     */
    private Database db;

    /**
     * The ServerSocket instance
     */
    private ServerSocket server;

    /**
     * The PokÃ©mons stored in memory
     */
    private ArrayList<Pokemon> pokemons;

    /**
     * Constructor
     *
     * @param port	the port on which the server should listen
     * @param fileName	the name of the file used for the database
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public PokemonBank() throws IOException, ClassNotFoundException {
        /*
		 * TODO
		 * Here, you should initialize the Database and ServerSocket instances.
         */

        db = new Database(DB_FILE_NAME);
        server = new ServerSocket(SERVER_PORT);

        System.out.println("Banque PokÃ©mon (" + DB_FILE_NAME + ") dÃ©marrÃ©e sur le port " + SERVER_PORT);

        // Let's load all the PokÃ©mons stored in database
        this.pokemons = this.db.loadPokemons();
        this.printState();

    }

    /**
     * The main loop logic of your application goes there.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("empty-statement")
    public void handleClient() throws IOException, ClassNotFoundException {

        System.out.println("En attente de connexion...");
        /*
		 * TODO
		 * Here, you should wait for a client to connect.
         */
        Socket clientConnect = server.accept();

        /*
		 * TODO
		 * You will one stream to read and one to write.
		 * Classes you can use:
		 * - ObjectInputStream
		 * - ObjectOutputStream
		 * - BankOperation
         */
        ObjectInputStream in = new ObjectInputStream(clientConnect.getInputStream());
        ObjectOutputStream out = new ObjectOutputStream(clientConnect.getOutputStream());

        // For as long as the client wants it
        boolean running = true;
        while (running) {
            /*
			 * TODO
			 * Here you should read the stream to retrieve a Request object
             */
            Request request;
            request = (Request) in.readObject();

            /*
			 * Note: the server will only respond with String objects.
             */
            switch (request) {
                case LIST:
                    System.out.println("Request: LIST");
                    if (this.pokemons.size() == 0) {
                        /*
					 * TODO
					 * There is no PokÃ©mons, so just send a message to the client using the output stream.
                         */
                        out.writeObject("Aucun pokemon");
                        out.flush();
                    } else {
                        /*
					 * TODO
					 * Here you need to build a String containing the list of PokÃ©mons, then write this String
					 * in the output stream.
					 * Classes you can use:
					 * - StringBuilder
					 * - String
					 * - the output stream
                         */
                        pokemons = db.loadPokemons();
                        StringBuilder sb = new StringBuilder();
                        for (int test = 0; test < pokemons.size(); test++) {
                            sb.append(pokemons.get(test));

                        }

                        out.writeObject(sb);
                        out.flush();
                    }
                    break;

                case STORE:
                    System.out.println("Request: STORE");
                    /*
				 * TODO
				 * If the client sent a STORE request, the next object in the stream should be a PokÃ©mon.
				 * You need to retrieve that PokÃ©mon and add it to the ArrayList.
                     */
                    pokemons.add((Pokemon) in.readObject());
                    out.flush();

                    /*
				 * TODO
				 * Then, send a message to the client so he knows his PokÃ©mon is safe.
                     */
                    out.writeObject("Pokemon is safe.");
                    out.flush();

                    break;

                case CLOSE:
                    System.out.println("Request: CLOSE");
                    /*
				 * TODO
				 * Here, you should use the output stream to send a nice 'Au revoir !' message to the client. 
                     */
                    out.writeObject("Au revoir !");
                    // Closing the connection
                    System.out.println("Fermeture de la connexion...");
                    running = false;
                    break;
            }

            this.printState();

            /*
		 * TODO
		 * Now you can close both I/O streams, and the client socket.
             */
            out.close();
            in.close();
            clientConnect.close();
            /*
		 * TODO
		 * Now that everything is done, let's update the database.
             */

            db.savePokemons(pokemons);
        }
    }

    /**
     * Print the current state of the bank
     */
    private void printState() {

        System.out.print("[");
        for (int i = 0; i < this.pokemons.size(); i++) {
            if (i > 0) {
                System.out.print(", ");
            }
            System.out.print(this.pokemons.get(i));
        }
        System.out.println("]");
    }

    /**
     * Stops the server. Note: This function will never be called in this
     * project.
     *
     * @throws IOException
     */
    public void stop() throws IOException {
        this.server.close();
    }

}
