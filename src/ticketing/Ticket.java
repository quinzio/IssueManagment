package ticketing;

public class Ticket {

	static enum State {
		Open, Assigned, Closed;
	}

	static enum Severity {
		Minor, Major, Critical, Blocking;
	}

	static Integer counter = 1;
	private Severity severity;

	private State state;
	private String description;
	private String path;

	private User reporter;
	private User maintainer;
	private Integer counter_instance;
	private String solutionDescription;

	public Ticket(User reporter, String path, String description, Severity severity) {
		this.description = description;
		this.severity = severity;
		this.path = path;
		this.counter_instance = counter++;
		this.state = State.Open;
	}

	public Ticket() {
	}

	public void assignMaintainer(User maintainer) {
		this.maintainer = maintainer;
		this.state = State.Assigned;
	}

	/*
	 * La classe Ticket offre i metodi getter getDescription(), getId(),
	 * getComponent(), getAuthor() e getSeverity().
	 */
	public String getDescription() {
		return description;
	}

	public Integer getId() {
		return counter_instance;
	}

	public String getComponent() {
		return path;
	}

	public User getAuthor() {
		return reporter;
	}

	public User getMaintainer() {
		return maintainer;
	}

	public Ticket.Severity getSeverity() {
		return severity;
	}

	/*
	 * La classe Ticket offre il metodo getter getState(), che dà lo stato
	 * corrente del ticket.
	 */
	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	/*
	 * Il metodo getSolutionDescription() della classe Ticket dà la descrizione
	 * della soluzione; lancia un'eccezione se il ticket non si trova nello
	 * stato Closed.
	 */
	public String getSolutionDescription() throws TicketException {
		if (this.state != Ticket.State.Closed) throw new TicketException();
		return solutionDescription;
	}

}
