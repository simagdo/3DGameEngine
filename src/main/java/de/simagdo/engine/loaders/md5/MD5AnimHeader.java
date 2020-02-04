package de.simagdo.engine.loaders.md5;

import java.util.List;

public class MD5AnimHeader {

    private String version;
    private String commandLine;
    private int numFrames;
    private int numJoints;
    private int frameRate;
    private int numAnimatedComponents;

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCommandLine() {
        return this.commandLine;
    }

    public void setCommandLine(String commandLine) {
        this.commandLine = commandLine;
    }

    public int getNumFrames() {
        return this.numFrames;
    }

    public void setNumFrames(int numFrames) {
        this.numFrames = numFrames;
    }

    public int getNumJoints() {
        return this.numJoints;
    }

    public void setNumJoints(int numJoints) {
        this.numJoints = numJoints;
    }

    public int getFrameRate() {
        return this.frameRate;
    }

    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }

    public int getNumAnimatedComponents() {
        return this.numAnimatedComponents;
    }

    public void setNumAnimatedComponents(int numAnimatedComponents) {
        this.numAnimatedComponents = numAnimatedComponents;
    }

    @Override
    public String toString() {
        return "animHeader: [version: " + this.version + ", commandLine: " + this.commandLine +
                ", numFrames: " + this.numFrames + ", numJoints: " + this.numJoints +
                ", frameRate: " + this.frameRate + ", numAnimatedComponents:" + this.numAnimatedComponents + "]";
    }

    public static MD5AnimHeader parse(List<String> lines) throws Exception {
        MD5AnimHeader header = new MD5AnimHeader();

        int numLines = lines != null ? lines.size() : 0;
        if (numLines == 0) {
            throw new Exception("Cannot parse empty file");
        }

        boolean finishHeader = false;
        for (int i = 0; i < numLines && !finishHeader; i++) {
            String line = lines.get(i);
            String[] tokens = line.split("\\s+");
            int numTokens = tokens != null ? tokens.length : 0;
            if (numTokens > 1) {
                String paramName = tokens[0];
                String paramValue = tokens[1];

                switch (paramName) {
                    case "MD5Version":
                        header.setVersion(paramValue);
                        break;
                    case "commandline":
                        header.setCommandLine(paramValue);
                        break;
                    case "numFrames":
                        header.setNumFrames(Integer.parseInt(paramValue));
                        break;
                    case "numJoints":
                        header.setNumJoints(Integer.parseInt(paramValue));
                        break;
                    case "frameRate":
                        header.setFrameRate(Integer.parseInt(paramValue));
                        break;
                    case "numAnimatedComponents":
                        header.setNumAnimatedComponents(Integer.parseInt(paramValue));
                        break;
                    case "hierarchy":
                        finishHeader = true;
                        break;
                }
            }
        }
        return header;
    }
}
