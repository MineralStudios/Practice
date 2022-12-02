package gg.mineral.practice.util.math;

public class MathUtil {
	private static final int DEFAULT_K_FACTOR = 25, WIN = 1, LOSS = 0;

	public static int getNewRating(int rating, int opponentRating, boolean won) {
		return MathUtil.getNewRating(rating, opponentRating, won ? MathUtil.WIN : MathUtil.LOSS);
	}

	public static int getNewRating(int rating, int opponentRating, int score) {
		double kFactor = MathUtil.getKFactor(rating);
		double expectedScore = MathUtil.getExpectedScore(rating, opponentRating);
		int newRating = MathUtil.calculateNewRating(rating, score, expectedScore, kFactor);

		return score == 1 && newRating == rating ? newRating++ : newRating;
	}

	private static int calculateNewRating(int oldRating, int score, double expectedScore, double kFactor) {
		return oldRating + (int) (kFactor * (score - expectedScore));
	}

	private static float getKFactor(int rating) {
		return DEFAULT_K_FACTOR;
	}

	private static double getExpectedScore(int rating, int opponentRating) {
		return 1 / (1 + Math.pow(10, ((double) (opponentRating - rating) / 400)));
	}

	public static int roundUp(int val, int multiple) {
		int mod = val % multiple;
		return mod == 0 ? val : val + multiple - mod;
	}
}
