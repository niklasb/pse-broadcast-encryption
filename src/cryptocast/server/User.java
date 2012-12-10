package cryptocast.server;

/**
 * Represents a user in our application.
 * @param <ID> The type of the user identities
 */
class User<ID> {
    //User's name.
    private String name;
    private ID id;

    /** Creates a User with the given attributes.
     * @param name The name of this user.
     * @param id The ID of this user.
     */
    public User(String name, ID id) {
        this.name = name;
        this.id = id;
    }

    /** @return the name of this user. */
    public String getName() { return name; }
    /** @return the id of this user. */
    public ID getIdentity() { return id; }
}
