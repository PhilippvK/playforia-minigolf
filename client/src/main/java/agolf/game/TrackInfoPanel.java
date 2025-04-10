package agolf.game;

import agolf.GameContainer;
import agolf.GolfGameFrame;
import com.aapeli.client.StringDraw;
import com.aapeli.colorgui.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class TrackInfoPanel extends Panel implements ActionListener {

    private static final Font fontDialog12 = new Font("Dialog", Font.PLAIN, 12);
    private static final Font fontDialog14 = new Font("Dialog", Font.PLAIN, 14);
    private static final Font fontSerif16 = new Font("Serif", Font.PLAIN, 16);
    private static final Font fontDialog11 = new Font("Dialog", Font.PLAIN, 11);
    private static final Color[] voteColours = new Color[] {
        new Color(192, 0, 0), new Color(255, 0, 0), new Color(255, 64, 0),
        new Color(255, 128, 0), new Color(255, 192, 0), new Color(255, 255, 0),
        new Color(192, 255, 0), new Color(128, 255, 0), new Color(64, 255, 0),
        new Color(0, 255, 0), new Color(64, 255, 64)
    };

    private GameContainer gameContainer;
    private int width;
    private int height;
    private boolean showLongAvgResult;
    private int numTracks;
    private int currentTrack;
    private String trackAuthor;
    private String trackName;
    private String firstBestPlayerName;
    private String firstBestPlayerDate;
    private String lastBestPlayerName;
    private String lastBestPlayerDate;
    private int resultBestNumStrokes;
    private int trackTotalRatings;
    private int trackRating;
    private double resultBestPercent;
    private double resultAverage;
    private double trackAverageRating;
    private Button buttonNoVote;
    private Button[] buttonsVote;
    private Button buttonReject;
    private Button buttonAccept;
    private Button buttonR;
    private ConfirmButton buttonBack;
    private boolean hasNotRatedTrack;
    private boolean trackPending;
    private boolean backButtonVisible;
    private boolean rButtonVisible;
    private Image image;
    private Graphics graphics;
    private boolean created;

    protected TrackInfoPanel(GameContainer gameContainer, int width, int height, boolean var4) {
        this.gameContainer = gameContainer;
        this.width = width;
        this.height = height;
        this.setSize(width, height);
        this.showLongAvgResult = var4;
        this.created = false;
        this.hasNotRatedTrack = false;
        this.trackPending = false;
        this.backButtonVisible = false;
        this.rButtonVisible = false;
        this.numTracks = -1;
        this.resetCurrentTrack();
    }

    public void addNotify() {
        super.addNotify();
        if (!this.created) {
            this.created = true;
            this.create();
        }

        this.repaint();
    }

    public void paint(Graphics g) {
        this.update(g);
    }

    public void update(Graphics g) {
        if (this.image == null) {
            this.image = this.createImage(this.width, this.height);
            this.graphics = this.image.getGraphics();
        }

        this.graphics.setColor(GolfGameFrame.colourGameBackground);
        this.graphics.fillRect(0, 0, this.width, this.height);
        if (this.numTracks > -1) {
            this.graphics.setFont(fontDialog12);
            this.graphics.setColor(GolfGameFrame.colourTextDarkGreen);
            int xMod = this.showLongAvgResult ? 20 : 0;
            if (this.currentTrack == -1) {
                StringDraw.drawString(
                        this.graphics,
                        this.gameContainer.textManager.getText("GameTrackInfo_NumberOfTracks", this.numTracks),
                        this.width / 4 - xMod,
                        15,
                        0);
            } else {
                StringDraw.drawString(
                        this.graphics,
                        this.gameContainer.textManager.getText(
                                "GameTrackInfo_CurrentTrack", this.currentTrack + 1, this.numTracks),
                        this.width / 4 - xMod,
                        15,
                        0);
                this.graphics.setColor(GolfGameFrame.colourTextBlack);

                Font font;
                for (font = fontSerif16;
                        this.getFontMetrics(font).stringWidth(this.trackName) / 2 > this.width / 4 - xMod;
                        font = new Font(font.getName(), font.getStyle(), font.getSize() - 1)) {}

                this.graphics.setFont(font);
                StringDraw.drawString(this.graphics, this.trackName, this.width / 4 - xMod, 35, 0);
                this.graphics.setFont(fontDialog14);
                this.graphics.setColor(GolfGameFrame.colourTextDarkGreen);
                StringDraw.drawString(this.graphics, this.trackAuthor, this.width / 4 - xMod, 55, 0);
                String keySuffix = this.showLongAvgResult ? "L" : "S";
                this.graphics.setFont(fontDialog12);
                this.graphics.setColor(GolfGameFrame.colourTextBlack);
                if (this.resultAverage > 0.0D) {
                    StringDraw.drawString(
                            this.graphics,
                            this.gameContainer.textManager.getText(
                                    "GameTrackInfo_AverageResult" + keySuffix,
                                    this.gameContainer.textManager.getNumber(this.resultAverage, 1)),
                            this.width * 3 / 4 - xMod,
                            this.lastBestPlayerName == null ? 15 : 12,
                            0);
                }

                String resultBestText;
                if (this.resultBestNumStrokes > 0) {
                    resultBestText = this.gameContainer.textManager.getText(
                            "GameTrackInfo_BestResult" + keySuffix, this.resultBestNumStrokes);
                    String resultText = resultBestText + " ";
                    if (this.resultBestPercent > 0.0D) {
                        byte roundingPrecision = 0;
                        if (this.resultBestPercent < 10.0D && this.resultBestPercent >= 1.0D) {
                            roundingPrecision = 1;
                        }

                        if (this.resultBestPercent < 1.0D && this.resultBestPercent >= 0.1D) {
                            roundingPrecision = 2;
                        }

                        if (this.resultBestPercent < 0.1D) {
                            roundingPrecision = 3;
                        }

                        resultText = resultText
                                + this.gameContainer.textManager.getText(
                                        "GameTrackInfo_BestResultPercent" + keySuffix,
                                        this.gameContainer.textManager.getNumber(
                                                this.resultBestPercent, roundingPrecision));
                    } else {
                        resultText = resultText
                                + this.gameContainer.textManager.getText("GameTrackInfo_BestResultUnique" + keySuffix);
                    }

                    this.graphics.setColor(GolfGameFrame.colourTextDarkGreen);
                    int textWidth = StringDraw.drawString(
                            this.graphics,
                            resultText,
                            this.width * 3 / 4 - xMod,
                            this.lastBestPlayerName == null ? 35 : 29,
                            0);
                    this.graphics.setColor(GolfGameFrame.colourTextBlack);
                    this.graphics.drawString(
                            resultBestText,
                            this.width * 3 / 4 - xMod - textWidth / 2,
                            this.lastBestPlayerName == null ? 35 : 29);
                    this.graphics.setClip(0, 0, this.width, this.height);
                    this.graphics.setFont(fontDialog11);
                    this.graphics.setColor(GolfGameFrame.colourTextDarkGreen);
                    resultBestText = this.firstBestPlayerName != null
                            ? this.gameContainer.textManager.getText(
                                    "GameTrackInfo_BestResultFirstBy" + keySuffix,
                                    this.firstBestPlayerName,
                                    this.firstBestPlayerDate)
                            : this.gameContainer.textManager.getText(
                                    "GameTrackInfo_BestResultFirstByUnknown" + keySuffix);
                    StringDraw.drawString(
                            this.graphics,
                            resultBestText,
                            this.width * 3 / 4 - xMod,
                            this.lastBestPlayerName == null ? 55 : 45,
                            2);
                    if (this.lastBestPlayerName != null) {
                        StringDraw.drawString(
                                this.graphics,
                                this.gameContainer.textManager.getText(
                                        "GameTrackInfo_BestResultLastBy" + keySuffix,
                                        this.lastBestPlayerName,
                                        this.lastBestPlayerDate),
                                this.width * 3 / 4 - xMod,
                                60,
                                2);
                    }

                    this.graphics.setColor(GolfGameFrame.colourTextBlack);
                }

                if (this.hasNotRatedTrack) {
                    this.graphics.drawString(
                            this.gameContainer.textManager.getText("GameTrackInfo_GiveRating"), 10, this.height - 4);
                }

                if (!this.hasNotRatedTrack && !this.trackPending && this.trackAverageRating >= 0.0D) {
                    this.graphics.setFont(fontDialog12);
                    StringDraw.drawString(
                            this.graphics,
                            this.gameContainer.textManager.getText(
                                    "GameTrackInfo_Rating",
                                    this.gameContainer.textManager.getNumber(this.trackAverageRating, 1)),
                            this.width / 4 - xMod,
                            this.height - 4,
                            0);
                }

                if (this.trackPending) {
                    this.graphics.drawString("Pending:", 10, this.height - 4);
                }
            }
        }

        g.drawImage(this.image, 0, 0, this);
    }

    public void actionPerformed(ActionEvent evt) {
        Object evtSource = evt.getSource();
        if (evtSource == this.buttonNoVote) {
            this.setHasNotRatedTrack(false);
            this.repaint();
        } else {
            for (int i = 0; i <= 10; ++i) {
                if (evtSource == this.buttonsVote[i]) {
                    this.gameContainer.gamePanel.rateTrack(this.currentTrack, i);
                    ++this.trackTotalRatings;
                    this.trackRating += i;
                    this.trackAverageRating = (double) this.trackRating / (double) this.trackTotalRatings;
                    this.setHasNotRatedTrack(false);
                    this.repaint();
                    return;
                }
            }

            if (evtSource == buttonReject || evtSource == buttonAccept) {
                this.gameContainer.gamePanel.respondNewGame(this.currentTrack, evtSource == this.buttonAccept);
                this.toggleAcceptRejectButtons(false);
                this.repaint();
            } else if (evtSource == this.buttonBack) {
                this.gameContainer.gamePanel.backToPrivate(this.currentTrack);
                this.setBackButtonVisible(false);
                this.repaint();
            } else if (evtSource == this.buttonR) {
                this.gameContainer.gamePanel.method336();
            }
        }
    }

    protected void resetCurrentTrack() {
        this.currentTrack = -1;
    }

    protected void setNumTracks(int numTracks) {
        this.numTracks = numTracks;
        this.repaint();
    }

    protected void parseTrackInfoStats(
            String trackAuthor,
            String trackName,
            int[] parStats,
            int[] someRatingStats,
            String firstBest,
            String lastBest,
            boolean trackTestMode1,
            boolean trackTestMode2,
            boolean var9) {
        ++this.currentTrack;
        this.trackAuthor = trackAuthor;
        this.trackName = trackName;
        this.resultBestNumStrokes = -1;
        this.resultAverage = this.trackAverageRating = -1.0D;
        String[] firstBestPlayer = this.parseBestPlayerInformation(firstBest);
        String[] lastBestPlayer = this.parseBestPlayerInformation(lastBest);
        this.firstBestPlayerName = firstBestPlayer[0];
        this.firstBestPlayerDate = firstBestPlayer[1];
        this.lastBestPlayerName = lastBestPlayer[0];
        this.lastBestPlayerDate = lastBestPlayer[1];

        if (parStats != null) {
            this.resultBestNumStrokes = parStats[2];
            if (parStats[3] > 1) { // If number of people to get the best score is more than 1 (not unique)
                this.resultBestPercent = 100.0D * (double) parStats[3] / (double) parStats[0];
            } else {
                this.resultBestPercent = 0.0D;
            }

            this.resultAverage = (double) parStats[1] / (double) parStats[0]; // Number completed?!?!
        }

        this.trackTotalRatings = this.trackRating = 0;
        if (someRatingStats != null) {
            for (int var12 = 1; var12 <= 9; ++var12) {
                this.trackTotalRatings += someRatingStats[var12];
                this.trackRating += var12 * someRatingStats[var12];
            }

            if (this.trackTotalRatings < 10) {
                this.trackTotalRatings += someRatingStats[0];
                this.trackTotalRatings += someRatingStats[10];
                this.trackRating += 10 * someRatingStats[10];
            }

            this.trackAverageRating =
                    (double) this.trackRating / (double) this.trackTotalRatings; // i think its average rating
        }

        if (!this.gameContainer.synchronizedTrackTestMode.get()) { // Toggles some buttons??!
            this.setHasNotRatedTrack(true);
        } else {
            this.toggleAcceptRejectButtons(trackTestMode1);
            this.setBackButtonVisible(trackTestMode2);
            this.setRButtonVisible(var9);
        }

        this.repaint();
    }

    protected void method384() {
        if (this.rButtonVisible) {
            this.setRButtonVisible(false);
        }
    }

    protected int method385() {
        return (int) (this.resultAverage + 0.99D);
    }

    private void create() {
        this.setLayout(null);
        this.buttonNoVote = new Button("-");
        this.buttonNoVote.setBounds(this.width - 264, this.height - 15, 22, 15);
        this.buttonNoVote.addActionListener(this);
        this.buttonsVote = new Button[11];

        for (int i = 0; i <= 10; ++i) {
            this.buttonsVote[i] = new Button(String.valueOf(i));
            this.buttonsVote[i].setBounds(this.width - 264 + 22 * (i + 1), this.height - 15, 22, 15);
            this.buttonsVote[i].setBackground(voteColours[i]);
            this.buttonsVote[i].addActionListener(this);
        }

        this.buttonReject = new Button("Reject");
        this.buttonReject.setBounds(this.width - 10 - 80 - 10 - 80, this.height - 15, 70, 15);
        this.buttonReject.setBackground(GolfGameFrame.colourButtonRed);
        this.buttonReject.addActionListener(this);
        this.buttonAccept = new Button("Accept");
        this.buttonAccept.setBounds(this.width - 10 - 80 - 10, this.height - 15, 70, 15);
        this.buttonAccept.setBackground(GolfGameFrame.colourButtonGreen);
        this.buttonAccept.addActionListener(this);
        this.buttonBack = new ConfirmButton("Back to private", "Sure?");
        this.buttonBack.setBounds(this.width - 10 - 130, this.height - 15, 95, 15);
        this.buttonBack.setBackground(GolfGameFrame.colourButtonRed);
        this.buttonBack.setActionListener(this);
        this.buttonR = new Button("R");
        this.buttonR.setBounds(this.width - 10 - 20 - 5, this.height - 15, 25, 15);
        this.buttonR.setBackground(GolfGameFrame.colourButtonGreen);
        this.buttonR.addActionListener(this);
    }

    private String[] parseBestPlayerInformation(String data) {
        String[] bestPlayerInformation = new String[] {null, null};
        if (data != null) {
            int splitPosition = data.indexOf(',');
            bestPlayerInformation[0] = data.substring(0, splitPosition);
            bestPlayerInformation[1] = data.substring(splitPosition + 1);
            splitPosition = bestPlayerInformation[1].indexOf(',');
            if (splitPosition > 0) {
                bestPlayerInformation[1] = bestPlayerInformation[1].substring(0, splitPosition);
            }

            long bestPlayerTimestamp = Long.parseLong(bestPlayerInformation[1]);
            bestPlayerInformation[1] = this.gameContainer.textManager.getDateWithTodayYesterday(bestPlayerTimestamp);
        }

        return bestPlayerInformation;
    }

    private void setHasNotRatedTrack(boolean hasNotRatedTrack) {
        if (hasNotRatedTrack != this.hasNotRatedTrack) {
            this.hasNotRatedTrack = hasNotRatedTrack;
            this.setVisible(false);
            if (hasNotRatedTrack) {
                this.add(this.buttonNoVote);

                for (int var2 = 0; var2 <= 10; ++var2) {
                    this.add(this.buttonsVote[var2]);
                }
            } else {
                this.removeAll();
            }

            this.setVisible(true);
        }
    }

    private void toggleAcceptRejectButtons(boolean trackPending) {
        if (!this.gameContainer.safeMode) {
            if (trackPending != this.trackPending) {
                this.trackPending = trackPending;
                this.setVisible(false);
                if (trackPending) {
                    this.add(this.buttonReject);
                    this.add(this.buttonAccept);
                } else {
                    this.remove(this.buttonReject);
                    this.remove(this.buttonAccept);
                }

                this.setVisible(true);
            }
        }
    }

    private void setBackButtonVisible(boolean backButtonVisible) {
        if (!this.gameContainer.safeMode) {
            if (backButtonVisible != this.backButtonVisible) {
                this.backButtonVisible = backButtonVisible;
                this.setVisible(false);
                if (backButtonVisible) {
                    this.add(this.buttonBack);
                } else {
                    this.remove(this.buttonBack);
                }

                this.setVisible(true);
            }
        }
    }

    private void setRButtonVisible(boolean rButtonVisible) {
        if (rButtonVisible != this.rButtonVisible) {
            this.rButtonVisible = rButtonVisible;
            this.setVisible(false);
            if (rButtonVisible) {
                this.add(this.buttonR);
            } else {
                this.remove(this.buttonR);
            }

            this.setVisible(true);
        }
    }
}
