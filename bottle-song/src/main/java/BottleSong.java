class BottleSong {

    String recite(int startBottles, int takeDown) {
        String result = "";
        for (int i = 0; i < takeDown; i++) {
            result += getBottleVerse(startBottles - i);
            if (i < takeDown - 1) {
                result += "\n";
            }
        }
        return result;
    }

    String getBottleVerse(int num) {
        String num1 = getNum(num).substring(0, 1).toUpperCase() + getNum(num).substring(1);
        String numLess = getNum(num - 1);

        String line1n2 = num1 + " green bottles hanging on the wall,\n";
        if (num == 1) {
            line1n2 = num1 + " green bottle hanging on the wall,\n";
        }
        String line3 = "And if one green bottle should accidentally fall,\n";
        String line4 = "";

        if (num > 2) {
            line4 = "There'll be "
                    + numLess + " green bottles hanging on the wall.\n";
        } else if (num == 2) {
            line4 = "There'll be "
                    + numLess + " green bottle hanging on the wall.\n";
        } else {
            line4 += "There'll be no green bottles hanging on the wall.\n";
        }

        StringBuilder sb = new StringBuilder();
        return sb.append(line1n2).append(line1n2).append(line3).append(line4).toString();
    }

    String getNum(int num) {
        String name = "";
        switch (num) {
            case 1:
                name = "one";
                break;

            case 2:
                name = "two";
                break;
            case 3:
                name = "three";
                break;
            case 4:
                name = "four";
                break;
            case 5:
                name = "five";
                break;
            case 6:
                name = "six";
                break;
            case 7:
                name = "seven";
                break;
            case 8:
                name = "eight";
                break;
            case 9:
                name = "nine";
                break;
            case 10:
                name = "ten";
                break;
            default:
                break;
        }
        return name;
    }

}
