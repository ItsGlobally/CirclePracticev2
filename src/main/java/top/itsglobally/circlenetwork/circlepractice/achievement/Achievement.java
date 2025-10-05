package top.itsglobally.circlenetwork.circlepractice.achievement;

public enum Achievement {
    JOIN("Welcome!", "Joined the server for the first time!"),
    FIRSTGAME("First Battle!", "Played your first game!");

    private final String title;
    private final String description;

    Achievement(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
