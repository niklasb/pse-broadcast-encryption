package cryptocast.server;

import java.io.Serializable;

/**
 * Represents a user in our application.
 * @param <ID> The type of the user identities
 */
public class User<ID> implements Serializable {
    private static final long serialVersionUID = 3943067597233965248L;

    private String name;
    private ID id;

    /** 
     * Creates a User with the given attributes.
     * 
     * @param name The name of this user.
     * @param id The ID of this user.
     */
    public User(String name, ID id) {
        this.name = name;
        this.id = id;
    }
    
    /** @return The name of this user */
    public String getName() { return name; }
    /** @return The id of this user */
    public ID getIdentity() { return id; }
}