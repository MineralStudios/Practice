package ms.uk.eclipse.util;

import net.jafama.FastMath;

public class MathUtil {
	private static final int DEFAULT_K_FACTOR = 25;
	private static final int WIN = 1;
	private static final int LOSS = 0;

	public static int getNewRating(int rating, int opponentRating, boolean won) {
		if (won) {
			return MathUtil.getNewRating(rating, opponentRating, MathUtil.WIN);
		}

		return MathUtil.getNewRating(rating, opponentRating, MathUtil.LOSS);
	}

	public static int getNewRating(int rating, int opponentRating, int score) {
		double kFactor = MathUtil.getKFactor(rating);
		double expectedScore = MathUtil.getExpectedScore(rating, opponentRating);
		int newRating = MathUtil.calculateNewRating(rating, score, expectedScore, kFactor);

		if (score == 1) {
			if (newRating == rating) {
				newRating++;
			}
		}
		return newRating;
	}

	private static int calculateNewRating(int oldRating, int score, double expectedScore, double kFactor) {
		return oldRating + (int) (kFactor * (score - expectedScore));
	}

	private static float getKFactor(int rating) {
		return DEFAULT_K_FACTOR;
	}

	private static double getExpectedScore(int rating, int opponentRating) {
		return 1 / (1 + FastMath.pow(10, ((double) (opponentRating - rating) / 400)));
	}

	public static int roundUp(int val, int multiple) {
		int mod = val % multiple;

		if (mod == 0) {
			return val;
		}

		val += (multiple - mod);

		return val;
	}
}
