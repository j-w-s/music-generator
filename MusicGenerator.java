import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class MusicGenerator {
    private static final int SAMPLE_RATE = 44100; // hz

    private static final Map<String, Double[]> NOTES_DATA = createNotesData();
    private static final Map<String, Double> NOTE_TYPES = createNoteTypes();
    private static final Map<String, String> NOTE_ALIASES = createNoteAliases();
    private static final Map<String, int[]> CHORD_INTERVALS = createChordIntervals();
    private static final String[] CHROMATIC_SCALE = {
            "C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B"
    };

    // base note frequencies + octaves 1-8
    private static Map<String, Double[]> createNotesData() {
        Map<String, Double[]> map = new HashMap<>();
        map.put("A", new Double[] { 27.50, 55.00, 110.00, 220.00, 440.00, 880.00, 1760.00, 3520.00, 7040.00 });
        map.put("Bb", new Double[] { 29.14, 58.27, 116.54, 233.08, 466.16, 932.33, 1864.66, 3729.31, 7458.62 });
        map.put("B", new Double[] { 30.87, 61.74, 123.47, 246.94, 493.88, 987.77, 1975.53, 3951.07, 7902.13 });
        map.put("C", new Double[] { 16.35, 32.70, 65.41, 130.81, 261.63, 523.25, 1046.50, 2093.00, 4186.01 });
        map.put("Db", new Double[] { 17.32, 34.65, 69.30, 138.59, 277.18, 554.37, 1108.73, 2217.46, 4434.92 });
        map.put("D", new Double[] { 18.35, 36.71, 73.42, 146.83, 293.66, 587.33, 1174.66, 2349.32, 4698.63 });
        map.put("Eb", new Double[] { 19.45, 38.89, 77.78, 155.56, 311.13, 622.25, 1244.51, 2489.02, 4978.03 });
        map.put("E", new Double[] { 20.60, 41.20, 82.41, 164.81, 329.63, 659.25, 1318.51, 2637.02, 5274.04 });
        map.put("F", new Double[] { 21.83, 43.65, 87.31, 174.61, 349.23, 698.46, 1396.91, 2793.83, 5587.65 });
        map.put("Gb", new Double[] { 23.12, 46.25, 92.50, 185.00, 369.99, 739.99, 1479.98, 2959.96, 5919.91 });
        map.put("G", new Double[] { 24.50, 49.00, 98.00, 196.00, 392.00, 783.99, 1567.98, 3135.96, 6271.93 });
        map.put("Ab", new Double[] { 25.96, 51.91, 103.83, 207.65, 415.30, 830.61, 1661.22, 3322.44, 6644.88 });
        return Collections.unmodifiableMap(map);
    }

    private static Map<String, Double> createNoteTypes() {
        Map<String, Double> map = new HashMap<>();
        map.put("w", 1.0);
        map.put("h", 0.5);
        map.put("qtr", 0.25);
        map.put("eigth", 0.125);
        map.put("sixteenth", 0.0625);
        return Collections.unmodifiableMap(map);
    }

    private static Map<String, String> createNoteAliases() {
        Map<String, String> map = new HashMap<>();
        map.put("A#", "Bb");
        map.put("C#", "Db");
        map.put("D#", "Eb");
        map.put("F#", "Gb");
        map.put("G#", "Ab");
        return Collections.unmodifiableMap(map);
    }

    private static Map<String, int[]> createChordIntervals() {
        Map<String, int[]> map = new HashMap<>();
        // basic triads
        map.put("Major", new int[] { 0, 4, 7 });
        map.put("Minor", new int[] { 0, 3, 7 });
        map.put("Diminished", new int[] { 0, 3, 6 });
        map.put("Augmented", new int[] { 0, 4, 8 });
        map.put("5", new int[] { 0, 7 });

        // seventh chords
        map.put("7", new int[] { 0, 4, 7, 10 });
        map.put("maj7", new int[] { 0, 4, 7, 11 });
        map.put("m7", new int[] { 0, 3, 7, 10 });
        map.put("m7b5", new int[] { 0, 3, 6, 10 });
        map.put("dim7", new int[] { 0, 3, 6, 9 });
        map.put("aug7", new int[] { 0, 4, 8, 10 });
        map.put("mMaj7", new int[] { 0, 3, 7, 11 });
        map.put("maj7b5", new int[] { 0, 4, 6, 11 });
        map.put("m7#5", new int[] { 0, 3, 8, 10 });

        // sixth chords
        map.put("6", new int[] { 0, 4, 7, 9 });
        map.put("m6", new int[] { 0, 3, 7, 9 });
        map.put("6/9", new int[] { 0, 4, 7, 9, 14 });
        map.put("m6/9", new int[] { 0, 3, 7, 9, 14 });

        // ninth chords
        map.put("9", new int[] { 0, 4, 7, 10, 14 });
        map.put("m9", new int[] { 0, 3, 7, 10, 14 });
        map.put("maj9", new int[] { 0, 4, 7, 11, 14 });
        map.put("7#9", new int[] { 0, 4, 7, 10, 15 });
        map.put("7b9", new int[] { 0, 4, 7, 10, 13 });
        map.put("aug9", new int[] { 0, 4, 8, 10, 14 });

        // extended/altered chords
        map.put("11", new int[] { 0, 4, 7, 10, 14, 17 });
        map.put("m11", new int[] { 0, 3, 7, 10, 14, 17 });
        map.put("13", new int[] { 0, 4, 7, 10, 14, 17, 21 });
        map.put("m13", new int[] { 0, 3, 7, 10, 14, 17, 21 });
        map.put("sus2", new int[] { 0, 2, 7 });
        map.put("sus4", new int[] { 0, 5, 7 });
        map.put("7sus4", new int[] { 0, 5, 7, 10 });
        map.put("maj7#11", new int[] { 0, 4, 7, 11, 14, 18 });
        map.put("7alt", new int[] { 0, 4, 7, 10, 13, 15, 21 }); // alt. dominant with b9, #9, b13
        map.put("7b13", new int[] { 0, 4, 7, 10, 14, 20 });
        map.put("13b9", new int[] { 0, 4, 7, 10, 13, 17, 21 });
        map.put("13sus4", new int[] { 0, 5, 7, 10, 14, 17, 21 });
        map.put("maj13", new int[] { 0, 4, 7, 11, 14, 17, 21 });

        return Collections.unmodifiableMap(map);
    }

    @FunctionalInterface
    interface WaveformGenerator {
        double[] generate(double frequency, double duration);
    }

    public static class Waveforms {
        // envelope parameters
        private static final double ATTACK_TIME = 0.02; // 20ms attack
        private static final double DECAY_TIME = 0.05; // 50ms decay
        private static final double SUSTAIN_LEVEL = 0.7; // 70% of peak amplitude
        private static final double RELEASE_TIME = 0.05; // 50ms release

        public static double[] generateSineWave(double frequency, double duration) {
            int length = (int) (SAMPLE_RATE * duration);
            double[] waveform = new double[length];
            for (int i = 0; i < length; i++) {
                double t = (double) i / SAMPLE_RATE;
                waveform[i] = 0.5 * Math.sin(2 * Math.PI * frequency * t);
            }
            return applyEnvelope(waveform);
        }

        public static double[] generateSquareWave(double frequency, double duration) {
            int length = (int) (SAMPLE_RATE * duration);
            double[] waveform = new double[length];
            for (int i = 0; i < length; i++) {
                double t = (double) i / SAMPLE_RATE;
                waveform[i] = 0.5 * Math.signum(Math.sin(2 * Math.PI * frequency * t));
            }
            return applyEnvelope(waveform);
        }

        public static double[] generateSawtoothWave(double frequency, double duration) {
            int length = (int) (SAMPLE_RATE * duration);
            double[] waveform = new double[length];
            for (int i = 0; i < length; i++) {
                double t = (double) i / SAMPLE_RATE;
                waveform[i] = 0.5 * (2 * ((frequency * t) % 1) - 1);
            }
            return applyEnvelope(waveform);
        }

        public static double[] generateTriangleWave(double frequency, double duration) {
            int length = (int) (SAMPLE_RATE * duration);
            double[] waveform = new double[length];
            for (int i = 0; i < length; i++) {
                double t = (double) i / SAMPLE_RATE;
                waveform[i] = 0.5 * (2 * Math.abs(2 * ((frequency * t) % 1) - 1) - 1);
            }
            return applyEnvelope(waveform);
        }

        private static double[] applyEnvelope(double[] waveform) {
            int length = waveform.length;
            int attackSamples = (int) (ATTACK_TIME * SAMPLE_RATE);
            int decaySamples = (int) (DECAY_TIME * SAMPLE_RATE);
            int releaseSamples = (int) (RELEASE_TIME * SAMPLE_RATE);

            attackSamples = Math.min(attackSamples, length / 4);
            decaySamples = Math.min(decaySamples, length / 4);
            releaseSamples = Math.min(releaseSamples, length / 4);

            double[] envelopedWave = new double[length];

            // attack
            for (int i = 0; i < attackSamples; i++) {
                double envelope = (double) i / attackSamples;
                envelopedWave[i] = waveform[i] * envelope;
            }

            // decay
            for (int i = attackSamples; i < attackSamples + decaySamples; i++) {
                double envelope = 1.0 - ((1.0 - SUSTAIN_LEVEL) * (i - attackSamples) / decaySamples);
                envelopedWave[i] = waveform[i] * envelope;
            }

            // sustain
            for (int i = attackSamples + decaySamples; i < length - releaseSamples; i++) {
                envelopedWave[i] = waveform[i] * SUSTAIN_LEVEL;
            }

            // release
            for (int i = length - releaseSamples; i < length; i++) {
                double envelope = SUSTAIN_LEVEL * (1.0 - (double) (i - (length - releaseSamples)) / releaseSamples);
                envelopedWave[i] = waveform[i] * envelope;
            }

            return normalize(envelopedWave);
        }

        private static double[] normalize(double[] waveform) {
            double max = Arrays.stream(waveform)
                    .map(Math::abs)
                    .max()
                    .orElse(1.0);
            return Arrays.stream(waveform)
                    .map(x -> x / max)
                    .toArray();
        }
    }

    public static class Note {
        private final String note;
        private final int octave;
        private final String noteType;
        private final double duration;
        private final double frequency;

        public Note(String note, int octave, String noteType) {
            this.note = note;
            this.octave = octave;
            this.noteType = noteType;
            this.duration = NOTE_TYPES.getOrDefault(noteType, 0.0);
            this.frequency = note != null ? getFrequency() : 0;
        }

        private double getFrequency() {
            String actualNote = NOTE_ALIASES.getOrDefault(note, note);
            if (NOTES_DATA.containsKey(actualNote)) {
                return NOTES_DATA.get(actualNote)[octave];
            }
            throw new IllegalArgumentException("invalid note: " + note);
        }

        public double[] generateWaveform(WaveformGenerator generator) {
            return note == null ? new double[(int) (duration * SAMPLE_RATE)] : generator.generate(frequency, duration);
        }
    }

    public static class Chord {
        private final List<Double> frequencies;
        private final double duration;

        public Chord(String rootNote, String chordName, String chordType, int octave) {
            if (!CHORD_INTERVALS.containsKey(chordName)) {
                throw new IllegalArgumentException("invalid chord: " + chordName);
            }

            this.duration = NOTE_TYPES.getOrDefault(chordType, 0.0);
            this.frequencies = generateChordFrequencies(rootNote, chordName, octave);
        }

        private List<Double> generateChordFrequencies(String rootNote, String chordName, int octave) {
            String actualRoot = NOTE_ALIASES.getOrDefault(rootNote, rootNote);
            if (!NOTES_DATA.containsKey(actualRoot)) {
                throw new IllegalArgumentException("invalid root note: " + rootNote);
            }

            List<Double> freqs = new ArrayList<>();
            int rootIdx = Arrays.asList(CHROMATIC_SCALE).indexOf(actualRoot);

            for (int interval : CHORD_INTERVALS.get(chordName)) {
                int noteIdx = (rootIdx + interval) % 12;
                int octaveAdj = (rootIdx + interval) / 12;
                int targetOctave = octave + octaveAdj;

                if (targetOctave >= 0 && targetOctave <= 8) {
                    String targetNote = CHROMATIC_SCALE[noteIdx];
                    freqs.add(NOTES_DATA.get(targetNote)[targetOctave]);
                }
            }

            return freqs;
        }

        public double[] generateWaveform(WaveformGenerator generator) {
            double[] chordWaveform = new double[(int) (SAMPLE_RATE * duration)];
            for (double frequency : frequencies) {
                double[] noteWaveform = generator.generate(frequency, duration);
                for (int i = 0; i < chordWaveform.length && i < noteWaveform.length; i++) {
                    chordWaveform[i] += noteWaveform[i];
                }
            }
            return Waveforms.normalize(chordWaveform);
        }
    }

    public static class Bar {
        private final List<Object> notes; // notes/chords
        private final String key;
        private final int[] timeSignature;

        public Bar(String key, int[] timeSignature) {
            this.key = key;
            this.timeSignature = timeSignature;
            this.notes = new ArrayList<>();
        }

        public void addNoteOrChord(Object item) {
            if (item instanceof Note || item instanceof Chord) {
                notes.add(item);
            } else {
                throw new IllegalArgumentException("item must be a note or chord");
            }
        }

        public double[] generateWaveform(WaveformGenerator generator) {
            List<double[]> waveforms = new ArrayList<>();
            for (Object note : notes) {
                if (note instanceof Note) {
                    waveforms.add(((Note) note).generateWaveform(generator));
                } else {
                    waveforms.add(((Chord) note).generateWaveform(generator));
                }
            }
            return concatenateWaveforms(waveforms);
        }
    }

    public static class Track {
        private final List<Bar> bars;
        private final String key;

        public Track(String key, int numberOfBars) {
            this.key = key;
            this.bars = new ArrayList<>();
        }

        public void addBar(Bar bar) {
            bars.add(bar);
        }

        public double[] generateWaveform(WaveformGenerator generator) {
            List<double[]> waveforms = new ArrayList<>();
            for (Bar bar : bars) {
                waveforms.add(bar.generateWaveform(generator));
            }
            return concatenateWaveforms(waveforms);
        }
    }

    public static class Song {
        private final List<Track> tracks;
        private final String key;
        private final int[] timeSignature;

        public Song(String key, int[] timeSignature) {
            this.key = key;
            this.timeSignature = timeSignature;
            this.tracks = new ArrayList<>();
        }

        public void addTrack(Track track) {
            tracks.add(track);
        }

        public double[] generateWaveform(WaveformGenerator generator) {
            List<double[]> trackWaveforms = new ArrayList<>();
            int maxLength = 0;

            // generate track waveforms and find max length
            for (Track track : tracks) {
                double[] waveform = track.generateWaveform(generator);
                trackWaveforms.add(waveform);
                maxLength = Math.max(maxLength, waveform.length);
            }

            // mix tracks together
            double[] songWaveform = new double[maxLength];
            for (double[] trackWaveform : trackWaveforms) {
                for (int i = 0; i < trackWaveform.length; i++) {
                    songWaveform[i] += trackWaveform[i];
                }
            }

            return Waveforms.normalize(songWaveform);
        }
    }

    // concatenate waveforms
    private static double[] concatenateWaveforms(List<double[]> waveforms) {
        int totalLength = waveforms.stream().mapToInt(w -> w.length).sum();
        double[] result = new double[totalLength];
        int currentPos = 0;
        for (double[] waveform : waveforms) {
            System.arraycopy(waveform, 0, result, currentPos, waveform.length);
            currentPos += waveform.length;
        }
        return result;
    }

    private static void playWaveform(double[] waveform) {
        try {
            AudioFormat format = new AudioFormat(44100, 16, 1, true, true);
            byte[] audioData = new byte[waveform.length * 2];
            for (int i = 0; i < waveform.length; i++) {
                short sample = (short) (waveform[i] * Short.MAX_VALUE);
                audioData[i * 2] = (byte) (sample >> 8);
                audioData[i * 2 + 1] = (byte) (sample & 0xFF);
            }

            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
            line.write(audioData, 0, audioData.length);
            line.drain();
            line.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Song createMaryHadALittleLamb() {
        // key of C major, 4/4 time signature
        Song song = new Song("C", new int[] { 4, 4 });

        // melody and accompaniment tracks
        Track melodyTrack = new Track("C", 8);
        Track chordTrack = new Track("C", 8);

        // melody bars (based)
        Bar melBar1 = new Bar("C", new int[] { 4, 4 });
        melBar1.addNoteOrChord(new Note("E", 4, "qtr"));
        melBar1.addNoteOrChord(new Note("D", 4, "qtr"));
        melBar1.addNoteOrChord(new Note("C", 4, "qtr"));
        melBar1.addNoteOrChord(new Note("D", 4, "qtr"));

        Bar melBar2 = new Bar("C", new int[] { 4, 4 });
        melBar2.addNoteOrChord(new Note("E", 4, "qtr"));
        melBar2.addNoteOrChord(new Note("E", 4, "qtr"));
        melBar2.addNoteOrChord(new Note("E", 4, "h"));

        Bar melBar3 = new Bar("C", new int[] { 4, 4 });
        melBar3.addNoteOrChord(new Note("D", 4, "qtr"));
        melBar3.addNoteOrChord(new Note("D", 4, "qtr"));
        melBar3.addNoteOrChord(new Note("D", 4, "h"));

        Bar melBar4 = new Bar("C", new int[] { 4, 4 });
        melBar4.addNoteOrChord(new Note("E", 4, "qtr"));
        melBar4.addNoteOrChord(new Note("G", 4, "qtr"));
        melBar4.addNoteOrChord(new Note("G", 4, "h"));

        Bar chordBar1 = new Bar("C", new int[] { 4, 4 });
        chordBar1.addNoteOrChord(new Chord("C", "Major", "h", 3));
        chordBar1.addNoteOrChord(new Chord("G", "7", "h", 3));

        Bar chordBar2 = new Bar("C", new int[] { 4, 4 });
        chordBar2.addNoteOrChord(new Chord("C", "Major", "w", 3));

        Bar chordBar3 = new Bar("C", new int[] { 4, 4 });
        chordBar3.addNoteOrChord(new Chord("G", "7", "w", 3));

        Bar chordBar4 = new Bar("C", new int[] { 4, 4 });
        chordBar4.addNoteOrChord(new Chord("C", "Major", "w", 3));

        // add bars to tracks
        melodyTrack.addBar(melBar1);
        melodyTrack.addBar(melBar2);
        melodyTrack.addBar(melBar3);
        melodyTrack.addBar(melBar4);

        chordTrack.addBar(chordBar1);
        chordTrack.addBar(chordBar2);
        chordTrack.addBar(chordBar3);
        chordTrack.addBar(chordBar4);

        // add tracks to song
        song.addTrack(melodyTrack);
        song.addTrack(chordTrack);

        return song;
    }

    public static void main(String[] args) {
        Song maryLamb = createMaryHadALittleLamb();
        System.out.println("Playing Mary Had a Little Lamb...");
        playWaveform(maryLamb.generateWaveform(Waveforms::generateSineWave));
    }

}