public class SceneLine {
    public Character speaker;
    public int dialogueIndex;
    public String position;
    public String narrationText;

    public SceneLine(Character speaker, int dialogIndex, String position) {
        this.speaker = speaker;
        this.dialogueIndex = dialogIndex;
        this.position = position;
    }

    public SceneLine(String narrationText) {
        this.speaker = null;
        this.narrationText = narrationText;
        this.dialogueIndex = -1;
    }
}