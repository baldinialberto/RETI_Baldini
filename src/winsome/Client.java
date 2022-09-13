package winsome;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class Client {
    private ClientInterface c_interface;
    private ServerProperties s_properties;
    private Socket socket;

    private User user;
    private boolean _on = false;
    private boolean _connected = false;
    private boolean _logged = false;

    public Client(String properties_filepath) {
        c_interface = new ClientInterface(this);
        s_properties = ServerProperties.readFile(properties_filepath);
        socket = new Socket();
        _on = true;
    }

    public boolean is_logged() {
        return _logged;
    }

    public boolean is_connected() {
        return _connected;
    }

    public boolean is_on() {
        return _on;
    }

    public void start_CLI() { c_interface.exec();}

    /**
     * Per inserire un nuovo utente, il server mette a disposizione una operazione
     * di registrazione di un utente. L’utente deve fornire username, password e una
     * lista di tag (massimo 5 tag). Il server risponde con un codice che può
     * indicare l’avvenuta registrazione, oppure, se lo username è già presente, o
     * se la password è vuota, restituisce un messaggio d’errore. Lo username
     * dell’utente deve essere univoco. Come specificato in seguito, le
     * registrazioni sono tra le informazioni da persistere lato server. Il comando
     * da digitare per la registrazione ha la sintassi seguente: register <username>
     * <password> <tags>, dove <tags> è una lista di tag separati da uno spazio.
     * Per semplicità i tag devono essere salvati in caratteri minuscoli. L’utente
     * deve poter scegliere liberamente la lista (il server non gestisce quindi una
     * lista predefinita di tag).
     * 
     * @param username
     * @param password
     * @param tags
     * @throws WinsomeExceptions.UsernameAlreadyExists
     */
    public void register(String username, String password, List<String> tags)
            throws WinsomeExceptions.UsernameAlreadyExists {
    }

    /**
     * Login di un utente già registrato per accedere al servizio. Il server
     * risponde con un codice che può indicare l’avvenuto login, oppure, se
     * l’utente ha già effettuato la login o la password è errata, restituisce un
     * messaggio d’errore. Il comando corrispondente è login <username> <password>.
     * 
     * @param username
     * @param password
     * @return
     * @throws WinsomeExceptions.WrongPassword
     */
    public User login(String username, String password)
            throws WinsomeExceptions.WrongPassword {
        throw new WinsomeExceptions.WrongPassword();
        // return null;
    }

    /**
     * Effettua il logout dell’utente dal servizio. Corrisponde al comando logout.
     * 
     * @throws NullPointerException
     */
    public void logout()
            throws NullPointerException {
    }

    /**
     * Utilizzata da un utente per visualizzare la lista (parziale) degli utenti
     * registrati al servizio. Il server restituisce la lista di utenti che hanno
     * almeno un tag in comune con l’utente che ha fatto la richiesta. Il comando
     * associato è list users.
     * 
     * @return
     * @throws NullPointerException
     */
    public List<String> listUsers()
            throws NullPointerException {
        return null;
    }

    /**
     * Operazione lato client per visualizzare la lista dei propri follower. Questo
     * comando dell’utente non scatena una richiesta sincrona dal client al server.
     * Il client restituisce la lista dei follower mantenuta localmente che viene
     * via via aggiornata grazie a notifiche “asincrone” ricevute dal server. Vedere
     * i dettagli di implementazione nella sezione successiva. Corrisponde al
     * comando list followers.
     * 
     * @return
     * @throws NullPointerException
     */
    public List<String> listFollowers()
            throws NullPointerException {
        return null;
    }

    /**
     * Utilizzata da un utente per visualizzare la lista degli utenti di cui è
     * follower. Questo metodo è corrispondente al comando list following.
     * 
     * @return
     * @throws NullPointerException
     */
    public List<String> listFollowing()
            throws NullPointerException {
        return null;
    }

    /**
     * L’utente chiede di seguire l’utente che ha per username idUser. Da quel
     * momento in poi può ricevere tutti i post pubblicati da idUser. Il comando
     * associato è follow <username>.
     * 
     * @param idUser
     * @return
     */
    public boolean followUser(String idUser) {
        return false;
    }

    /**
     * L’utente chiede di non seguire più l’utente che ha per username idUser. Il
     * comando associato è unfollow <username>.
     * 
     * @param idUser
     * @return
     */
    public boolean unfollowUser(String idUser) {
        return false;
    }

    /**
     * operazione per recuperare la lista dei post di cui l’utente è autore. Viene
     * restituita una lista dei post presenti nel blog dell’utente. Per ogni post
     * viene fornito id del post, autore e titolo. Viene attivata con il comando
     * blog.
     * 
     * @return
     */
    public List<Post> viewBlog() {
        return null;
    }

    /**
     * Operazione per pubblicare un nuovo post. L’utente deve fornire titolo e
     * contenuto del post. Il titolo ha lunghezza massima di 20 caratteri e il
     * contenuto una lunghezza massima di 500 caratteri. Se l’operazione va a buon
     * fine, il post è creato e disponibile per i follower dell’autore del post.
     * Il sistema assegna un identificatore univoco a ciascun post creato (idPost).
     * Il comando che l’utente digita per creare un post ha la seguente sintassi:
     * post <title> <content>.
     * 
     * @param titolo
     * @param contenuto
     * @return
     */
    public Post createPost(Post.Title titolo, Post.Content contenuto) {
        return null;
    }

    /**
     * Operazione per recuperare la lista dei post nel proprio feed. Viene
     * restituita una lista dei post. Per ogni post viene fornito id, autore e
     * titolo del post. La funzione viene attivata mediante il comando show feed.
     * 
     * @return
     */
    public List<Post> showFeed() {
        return null;
    }

    /**
     * Il server restituisce titolo, contenuto, numero di voti positivi, numero di
     * voti negativi e commenti del post. Se l’utente è autore del post può
     * cancellare il post con tutto il suo contenuto associato (commenti e voti). Se
     * l’utente ha il post nel proprio feed può esprimere un voto, positivo o
     * negativo (solo un voto, successivi tentativi di voto non saranno accettati
     * dal server, che restituirà un messaggio di errore) e/o inserire un commento.
     * Il comando digitato dall’utente è show post <id>.
     * 
     * @param idPost
     * @return
     */
    public Post showPost(String idPost) {
        return null;
    }

    /**
     * Operazione per cancellare un post. La richiesta viene accettata ed eseguita
     * solo se l’utente è l’autore del post. Il server cancella il post con tutto
     * il suo contenuto associato (commenti e voti). Non vengono calcolate
     * ricompense “parziali”, ovvero se un contenuto recente (post, voto o commento)
     * non era stato conteggiato nel calcolo delle ricompense perché ancora il
     * periodo non era scaduto, non viene considerato nel calcolo delle ricompense.
     * Il comando corrispondente alla cancellazione è delete <idPost>.
     * 
     * 
     * @param idPost
     * @return
     */
    public boolean deletePost(String idPost) {
        return false;
    }

    /**
     * operazione per effettuare il rewin di un post, ovvero per pubblicare nel
     * proprio blog un post presente nel proprio feed. La funzione viene attivata
     * mediante il comando rewin <idPost>.
     * 
     * @param idPost
     * @return
     */
    public boolean rewinPost(String idPost) {
        return false;
    }

    /**
     * Operazione per assegnare un voto positivo o negativo ad un post. Se l’utente
     * ha il post nel proprio feed e non ha ancora espresso un voto, il voto viene
     * accettato, negli altri casi (ad es. ha già votato il post, non ha il post
     * nel proprio feed, è l’autore del post) il voto non viene accettato e il
     * server restituisce un messaggio di errore. Il comando per assegnare un voto
     * al post è rate <idPost> <vote>. Nel comando i voti sono così codificati:
     * voto positivo +1, voto negativo -1.
     * 
     * @param idPost
     * @param voto
     * @return
     */
    public boolean ratePost(String idPost, boolean voto) {
        return false;
    }

    /**
     * Operazione per aggiungere un commento ad un post. Se l’utente ha il post nel
     * proprio feed, il commento viene accettato, negli altri casi (ad es. l’utente
     * non ha il post nel proprio feed oppure è l’autore del post) il commento non
     * viene accettato e il server restituisce un messaggio di errore. Un utente
     * può aggiungere più di un commento ad un post. La sintassi utilizzata dagli
     * utenti per commentare è: comment <idPost> <comment>.
     * 
     * @param idPost
     * @param comment
     * @return
     */
    public boolean addComment(String idPost, Comment comment) {
        return false;
    }

    /**
     * Operazione per recuperare il valore del proprio portafoglio. Il server
     * restituisce il totale e la storia delle transazioni (ad es. <incremento>
     * <timestamp>). Il comando corrispondente è wallet.
     * 
     * @return
     */
    public Wallet getWallet() {
        return null;
    }

    /**
     * Operazione per recuperare il valore del proprio portafoglio convertito in
     * bitcoin. Il server utilizza il servizio di generazione di valori random
     * decimali fornito da RANDOM.ORG per ottenere un tasso di cambio casuale e
     * quindi calcola la conversione. L’operazione è eseguita mediante il comando:
     * wallet btc.
     * 
     * @return
     */
    public float getWalletInBitcoin() {
        return 0.0f;
    }

    public void test() {
        System.out.println("Client:test()");
    }

    public Post create_post(String title, String content) throws WinsomeExceptions.UserNotLogged {
        if (user == null) {
            throw new WinsomeExceptions.UserNotLogged();
        }
        return new Post(this.user, title, content);
    }

    public Comment create_comment(String comment) throws WinsomeExceptions.UserNotLogged {
        if (user == null) {
            throw new WinsomeExceptions.UserNotLogged();
        }
        return new Comment(this.user, comment);
    }

    public void exit() {
        _on = false;
    }


}