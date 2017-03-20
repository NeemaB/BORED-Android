package cpen391.team6.bored.Items;

/**
 * Created by neema on 2017-03-20.
 */
public enum Command {

    /* List of commands that we can send to BORED */
    CHANGE_COLOUR,
    CHANGE_PEN_WIDTH,
    DRAW_LINE,
    FILL,
    REDO,
    UNDO,
    CLEAR,
    TERMINATE;

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

            case DRAW_LINE:

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

            case FILL:

                /* Send the 'F' character accompanied with the X and Y co-ordinates of the
                 * location to fill
                 */
                command = "F";
                for(int j = 0; j < params.length; j++){
                    for(int i = 0; i < 3 - params[j].toString().length(); j++){
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
        }

        return command;
    }

}
