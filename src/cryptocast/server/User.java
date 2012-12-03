package cryptocast.server;

/**
 * This Class represents an User.
 */
class User {
	//User's name.
    private String name;
    
    /** Creates a User with the given name.
     * @param name name of the user
     */
    public User(String name) { 
        this.name = name;
    }
    
    /**
     * @return Returns the name of this user.
     */
    public String getName() {
		return name; 
	}
}