package Main;
public class Time {

    GamePanel gp;

    // Internal: 0–23 hours, 0–59 minutes
    public int hour = 5;
    int minute = 0;
    int day = 0;
    int month = 0;
    int year = 0;


    public Time(GamePanel gp) {
        this.gp = gp;
    }
    public void updateTime() {
        // hour++; // debug: skip by hour
        minute += 30;
        // minute += 10; // real-time increment
        if (minute >= 60) {
            minute = 0;
            hour++;
        }
        if (hour >= 24) {
            hour = 0;
            day++;
            if (day >= 30) {
                day = 0;
                month++;
                if (month >= 4) {
                    month = 0;
                    year++;
                }
            }
        }
    }

    public void reset() {
        minute = 0;
        hour = 5;
        day = 1;
        month = 1;
        year = 1;
    }

    public void setToMorning() {
        System.out.println("Sleeping through the night...");

        //Reset npc interactions
        gp.ui.dailyBalinDialogueIndex = 0;

        hour = 6;
        minute = 0;
        day++;
        if (day >= 30) {
            day = 0;
            month++;
            if (month >= 4) {
                month = 0;
                year++;
            }
        }
    }

    public String getTimeString() {
        String meridiem = hour < 12 ? "AM" : "PM";
        int displayHour = hour % 12;
        if (displayHour == 0) displayHour = 12;
        return displayHour + ":" + String.format("%02d", minute) + " " + meridiem;
    }

    public boolean isNight() {
        return hour >= 20 || hour < 6;
    }

    public String getDateString() {
        String[] seasons = {"Spring", "Summer", "Fall", "Winter"};
        return seasons[month] + " " + day + ", Year " + year;
    }
}

