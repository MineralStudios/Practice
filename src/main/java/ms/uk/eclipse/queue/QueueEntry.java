package ms.uk.eclipse.queue;

import ms.uk.eclipse.gametype.Gametype;

public class QueueEntry {
	Queuetype q;
	Gametype g;

	public QueueEntry(Queuetype q, Gametype g) {
		this.q = q;
		this.g = g;
	}

	public boolean equals(QueueEntry qd) {
		return this.q.equals(qd.getQueuetype()) && this.g.equals(qd.getGametype());
	}

	public Queuetype getQueuetype() {
		return q;
	}

	public Gametype getGametype() {
		return g;
	}
}
