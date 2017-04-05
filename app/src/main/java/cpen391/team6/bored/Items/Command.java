package cpen391.team6.bored.Items;

/**
 * Created by neema on 2017-03-20.
 */
public enum Command {

    /* List of commands that we can send to BORED */
    CHANGE_COLOUR,
    CHANGE_PEN_WIDTH,
    START_DRAWING,
    POINT,
    STOP_DRAWING,
    FILL,
    REDO,
    UNDO,
    CLEAR,
    TERMINATE;
    //START_TRANSFER,
    //TRANSFER;

    /***********************************************************************************************
     * Construct the string representation of a command, any command data sent to the NIOS II will
     * be in a format dictated by this function
     *
     * @param cmd The command that we want to issue, this will be selected from the list of commands
     *            above
     *
     * @param params A variable size array containing all the additional parameters that we need
     *               to issue the command ie pixel co-ordinates or indices
     * @return
     **********************************************************************************************/
    public static String createCommand(Command cmd, Object ... params){

        String command = null;

        switch(cmd){

            case CHANGE_COLOUR:

                /* Send the 'C' character accompanied with the value of the
                 * colour index in the range 0 - 15
                 */
                command = "C";
                if(params[0].toString().length() == 1){
                    command += "0" + params[0].toString();
                }else{
                    command += params[0].toString();
                }
                break;

            case CHANGE_PEN_WIDTH:

                /* Send the 'S' character accompanied with the walue of the pen size 0 - 2 */
                command = "S" + params[0].toString();
                break;

            case START_DRAWING:

                /* Send the 'D' character accompanied with the X and Y co-ordinates of the
                 * old point and the new point to draw the line between
                 */
                command = "D";

               /* We need to send character representations of each
                * number, to do this check the length of the index (as a string)
                * and append the appropriate number of '0' characters so we
                * are always sending 3 characters for the index parameter
                */

                for(int j = 0; j < params.length; j++) {
                    for (int i = 0; i < 3 - params[j].toString().length(); i++) {
                        command += "0";
                    }
                    command += params[j].toString();
                }
                break;

            case POINT:

                /* Send the 'P' character accompanied with the X and Y co-ordinates of the
                 * first point to start drawing lines from
                 */
                command = "P";

                for(int j = 0; j < params.length; j++) {
                    for (int i = 0; i < 3 - params[j].toString().length(); i++) {
                        command += "0";
                    }
                    command += params[j].toString();
                }
                break;

            case STOP_DRAWING:

                /* Send the 'L' character to indicate that this is the last point to draw
                 * on the NIOS II
                 */

                command = "L";
                break;


            case FILL:

                /* Send the 'F' character accompanied with the X and Y co-ordinates of the
                 * location to fill
                 */
                command = "F";
                for(int j = 0; j < params.length; j++){
                    for(int i = 0; i < 3 - params[j].toString().length(); i++){
                        command += "0";
                    }
                    command += params[j].toString();
                }
                break;

            case REDO:

                command = "R";
                break;

            case UNDO:
                command = "U";
                break;

            case CLEAR:
                command = "X";
                break;

            case TERMINATE:
                command = "Z";
                break;

            /*case START_TRANSFER:
                command = "V";
                break;

            case TRANSFER:
                command = String.valueOf(params[0].toString().length());
                command += params[0].toString();
                if ((Integer)params[1] < 10) command += "0";
                command += params[1].toString();
                break;*/
        }

        return command;
    }

}
