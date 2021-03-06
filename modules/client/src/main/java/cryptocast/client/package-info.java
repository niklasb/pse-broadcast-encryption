/**
 * The client contains an instance of {@link FileChooser}, which can be used to select any file from the SD card 
 * of an Android gadget. Since a private key is necessary to encrypt data received from a server the 
 * {@link FileChooser} is used to select a keyfile. All servers and responding keyfiles are placed 
 * in an instance of {@link ServerHistory} and therefore can be saved and loaded after closing the client.
 * Every error is displayed by using a simple pop up window defined by the class {@link ErrorFragment}.
 */
package cryptocast.client;
import cryptocast.client.filechooser.FileChooser;

