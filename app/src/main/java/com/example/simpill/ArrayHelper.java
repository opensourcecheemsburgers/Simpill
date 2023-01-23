/* (C) 2023 */
package com.example.simpill;

public class ArrayHelper {

    public static final String STR_SEPARATOR = ", ";

    public String[] convertStringToArray(String str) {
        return str.split(STR_SEPARATOR);
    }

    public String[] sortTimeArray(String[] timeArray) {
        DateTimeManager dateTimeManager = new DateTimeManager();

        for (int arraySortAttempt = 0; arraySortAttempt < timeArray.length; arraySortAttempt++) {
            for (int currentNumber = 0; currentNumber < timeArray.length - 1; currentNumber++) {
                int nextNumber = currentNumber + 1;

                long currentArrIndexTime =
                        dateTimeManager.convertTimeToCurrentDateTimeInMillis(
                                timeArray[currentNumber]);
                long nextArrIndexTime =
                        dateTimeManager.convertTimeToCurrentDateTimeInMillis(
                                timeArray[currentNumber]);

                String currentTime = dateTimeManager.formatLongAsTimeString(currentArrIndexTime);
                String nextTime = dateTimeManager.formatLongAsTimeString(nextArrIndexTime);

                if (currentArrIndexTime > nextArrIndexTime) {
                    timeArray[currentNumber] = nextTime;
                    timeArray[nextNumber] = currentTime;
                }
            }
        }
        return timeArray;
    }

    public String convertArrayToString(String[] array) {
        StringBuilder timeArrayAsString = new StringBuilder();
        for (int currentArrayNumber = 0; currentArrayNumber < array.length; currentArrayNumber++) {
            timeArrayAsString.append(array[currentArrayNumber]);

            if (currentArrayNumber < array.length - 1) {
                timeArrayAsString.append(STR_SEPARATOR);
            }
        }
        return timeArrayAsString.toString();
    }

    public String[] convert24HrArrayTo12HrArray(String[] array) {
        for (int currentArrayNumber = 0; currentArrayNumber < array.length; currentArrayNumber++) {
            array[currentArrayNumber] =
                    new DateTimeManager().convert24HrTimeTo12HrTime(array[currentArrayNumber]);
        }

        return array;
    }

    public int findPillUsingPrimaryKey(Pill[] pillArray, int primaryKey) {
        int pillIndex = -1;
        for (int index = 0; index < pillArray.length; index++) {
            if (pillArray[index].getPrimaryKey() == primaryKey) pillIndex = index;
        }
        return pillIndex;
    }

    public Pill[] deletePillFromPillArray(Pill[] pillArray, Pill pill) {
        int index = findPillUsingPrimaryKey(pillArray, pill.getPrimaryKey());
        Pill[] pillArrayCopy = new Pill[pillArray.length - 1];
        System.arraycopy(pillArray, 0, pillArrayCopy, 0, index);
        System.arraycopy(pillArray, index + 1, pillArrayCopy, index, pillArray.length - index - 1);
        return pillArrayCopy;
    }

    public Pill[] addPillToPillArray(Pill[] pillArray, Pill pill) {
        Pill[] pillArrayCopy = new Pill[pillArray.length + 1];
        System.arraycopy(pillArray, 0, pillArrayCopy, 0, pillArray.length);
        pillArrayCopy[pillArrayCopy.length - 1] = pill;
        return pillArrayCopy;
    }
}
