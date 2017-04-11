package ticketing;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import ticketing.Ticket.Severity;

/**
 * @author MUNARID
 *
 */
public class IssueManager {
	Set<User> users = new HashSet<User>();
	Set<Component> components = new HashSet<Component>();
	Set<Ticket> tickets = new HashSet<Ticket>();

	/**
	 * Il metodo createUser() riceve uno username e il set dei ruoli
	 * (UserClasses) che l'utente svolge. In alternativa al set si può usare una
	 * lista variabile di argomenti. In entrambi i casi il metodo lancia
	 * un'eccezione se lo username è già stato inserito o se nessun ruolo è
	 * indicato.
	 * 
	 * @param userName
	 * @param role
	 * @throws TicketException
	 */
	public void createUser(String userName, User.UserClass role)
			throws TicketException {
		if (users.stream().filter(u -> u.getUserName().equals(userName))
				.count() > 0)
			throw new TicketException("User already in database");
		if (role == null)
			throw new TicketException("No role assigned");
		users.add(new User(userName, role));
	}

	public void createUser(String userName, Set<User.UserClass> role) {
		users.add(new User(userName, role));
	}

	/*
	 * Dato uno username si può ottenere il set dei ruoli dell'utente
	 * corrispondente con il metodo getUserClasses().
	 */
	public Set<User.UserClass> getUserClasses(String userName) {
		for (User u : users) {
			if (u.getUserName().equals(userName)) {
				return u.getUserClasses();
			}
		}
		return null;
	}

	/*
	 * Il metodo defineComponent() genera un nuovo componente dato il nome e
	 * lancia un'eccezione se esiste già un componente con quel nome.
	 */
	public void defineComponent(String name) throws TicketException {
		for (Component c : components) {
			if (c.getComponentName().equals(name))
				throw new TicketException("Duplicate Component");
		}
		components.add(new Component(name));
	}

	/*
	 * Il metodo defineSubComponent() genera un nuovo sotto-componente dati il
	 * nome e il path che identifica il predecessore (componente o
	 * sotto-componente) di cui il nuovo elemento diventa sotto-componente.
	 * Lancia un'eccezione se il predecessore non esiste o se ha giï¿½ un
	 * sotto-componente con lo stesso nome. Esempio: dato il sistema in figura,
	 * per aggiungere SubC al componente System si scrive:
	 * tm.defineSubComponent("SubC","\System");, mentre per aggiungere SubB.2 si
	 * scrive: tm.defineSubComponent("SubB.2","\System\SubB");.
	 */
	public void defineSubComponent(String newSubcomponent, String path)
			throws TicketException {
		Set<Component> lc = components;
		String[] sa = path.split("/");
		String[] sa1 = Arrays.copyOfRange(sa, 1, sa.length);
		for (String s : sa1) {
			boolean found = false;
			for (Component c : lc) {
				if (c.getComponentName().equals(s)) {
					lc = c.getSubComponents();
					found = true;
					break;
				}
			}
			if (!found)
				throw new TicketException("Component not found in string");
		}
		lc.add(new Component(newSubcomponent));
	}

	/*
	 * Dato il path di un elemento (componente o sotto-componente) si può
	 * ottenere il set dei nomi dei sotto-componenti con il metodo
	 * getSubComponents() e il nome del predecessore con il metodo
	 * getParentComponent() (che dà null se l'elemento non ha un predecessore).
	 */
	public Set<Component> getSubComponents(String path) throws TicketException {
		Set<Component> lc = components;
		String[] sa = path.split("/");
		String[] sa1 = Arrays.copyOfRange(sa, 1, sa.length);
		for (String s : sa1) {
			boolean found = false;
			for (Component c : lc) {
				if (c.getComponentName().equals(s)) {
					lc = c.getSubComponents();
					found = true;
					break;
				}
			}
			if (!found)
				throw new TicketException("Component not found in string");
		}
		return lc;
	}

	// getParentComponent("/System/SubB/SubB.2")
	public Component getParentComponent(String path) throws TicketException {
		Set<Component> lc = components;
		Component c0 = null, c1 = null, c2 = null;
		String[] sa = path.split("/");
		String[] sa1 = Arrays.copyOfRange(sa, 1, sa.length);
		for (String s : sa1) {
			boolean found = false;
			for (Component c : lc) {
				c0 = c;
				if (c.getComponentName().equals(s)) {
					lc = c.getSubComponents();
					found = true;
					break;
				}
			}
			if (!found)
				throw new TicketException("Component not found in string");
			c2 = c1;
			c1 = c0;
		}
		return c2;

	}

	/*
	 * openTicket("alpha", "/System/SubA", "Initial menu does not show 'open'
	 * item", Ticket.Severity.Major)
	 * 
	 * Un ticket è aperto con il metodo openTicket() che riceve lo username
	 * dell'utente, il path dell'elemento difettoso, la descrizione
	 * dell'anomalia e la severità (Severity) della stessa. Il metodo dà un id
	 * univoco (intero progressivo a partire da 1) per il ticket. Lancia
	 * un'eccezione se lo username non è valido, il path non identifica alcun
	 * 
	 * elemento, o se l'utente non svolge il ruolo Reporter.
	 * ts.openTicket("alpha", "/System/SubA",
	 * "Initial menu does not show 'open' item", Ticket.Severity.Major);
	 */
	public int openTicket(String reporter, String reportedComponent,
			String descriprion, Ticket.Severity severity)
			throws TicketException {
		Map<String, User> map1 = new TreeMap<>();
		users.forEach(u -> {
			map1.put(u.getUserName(), u);
			return;
		});
		if (!map1.containsKey(reporter))
			throw new TicketException("username non valido");
		if (!map1.get(reporter).getUserClasses()
				.contains(User.UserClass.Reporter))
			throw new TicketException("user is not a Reporter");

		Ticket t;
		tickets.add(t = new Ticket(map1.get(reporter), reportedComponent,
				descriprion, severity));

		return t.getId();

	}

	/*
	 * Il metodo assignTicket() riceve un ticket id e uno username: porta lo
	 * stato del ticket a Assigned e collega il ticket all'utente come
	 * assegnatario del ticket. Lancia un'eccezione se il ticket id o lo
	 * username non sono validi, o se l'utente non svolge il ruolo Maintainer.
	 */
	public void assignTicket(Integer id, String maintainer)
			throws TicketException {
		Map<Integer, Ticket> map1 = new TreeMap<>();
		tickets.forEach(t -> {
			map1.put(t.getId(), t);
			return;
		});
		if (!map1.containsKey(id))
			throw new TicketException("id non valido");

		Map<String, User> map2 = new TreeMap<>();
		users.forEach(u -> {
			map2.put(u.getUserName(), u);
			return;
		});
		if (!map2.containsKey(maintainer))
			throw new TicketException("username non valido");
		if (!map2.get(maintainer).getUserClasses()
				.contains(User.UserClass.Maintainer))
			throw new TicketException("user is not a maintainer");

		map1.get(id).assignMaintainer(map2.get(maintainer));

	}

	/*
	 * Il metodo getTicket() riceve un id e dà l'oggetto Ticket corrispondente
	 * oppure null se l'id non è valido.
	 */
	public Ticket getTicket(int id) throws TicketException {
		Map<Integer, Ticket> map1 = new TreeMap<>();
		tickets.forEach(t -> {
			map1.put(t.getId(), t);
			return;
		});
		if (!map1.containsKey(id))
			throw new TicketException("id non valido");

		return map1.get(id);

	}

	/*
	 * Il metodo getAllTickets() dà la lista dei ticket ordinata naturalmente
	 * (si legga la nota).
	 */
	public List<Ticket> getAllTickets() {
		return tickets
				.stream()
				.sorted(Comparator
						.<Ticket, Ticket.Severity> comparing((Ticket t) -> t
								.getSeverity())).collect(Collectors.toList());
	}

	/*
	 * Il metodo closeTicket() riceve un ticket id e la descrizione della
	 * soluzione, e porta lo stato del ticket a Closed. Lancia un'eccezione se
	 * il ticket non si trova nello stato Assigned.
	 */

	public void closeTicket(int id, String description) throws TicketException {
		if (tickets.stream().filter(t -> t.getId() == id).count() != 1)
			throw new TicketException();
		Ticket tr = new Ticket();
		tr = tickets.stream().filter(t -> t.getId() == id)
				.reduce(tr, (t1, t2) -> t2);
		if (tr.getState() == Ticket.State.Assigned)
			tr.setState(Ticket.State.Closed);
		else
			throw new TicketException();

	}

	/*
	 * Il metodo countBySeverityOfState() dato uno stato dei ticket fornisce una
	 * mappa con il numero di ticket per Severity, considerando soltanto i
	 * ticket in quello stato oppure tutti i ticket se l'argomento è nullo. La
	 * mappa è ordinata in base alla Severity.
	 */
	public SortedMap<Ticket.State, SortedMap<Ticket.Severity, Long>> countBySeverityOfState(
			Ticket.State state) {
		// return
		// tickets.stream().collect(Collectors.groupingBy(Ticket::getSeverity,
		// TreeMap::new, Collectors.toList()));
		// SortedMap<Ticket.Severity, Set<Ticket>> m = tickets.stream().collect(
		// Collectors.groupingBy(Ticket::getSeverity, TreeMap::new,
		// Collectors.toCollection(TreeSet::new)));
		if (state != null) {
			Predicate<? super Ticket> pred = t -> t.getState() == state;
			SortedMap<Ticket.Severity, Long> m = tickets
					.stream()
					.filter(pred)
					.collect(
							Collectors.groupingBy(Ticket::getSeverity,
									TreeMap::new, Collectors.counting()));
			SortedMap<Ticket.State, SortedMap<Ticket.Severity, Long>> m2 = new TreeMap<Ticket.State, SortedMap<Ticket.Severity, Long>>();
			m2.put(state, m);
			return m2;
		} else {
			SortedMap<Ticket.State, SortedMap<Ticket.Severity, Long>> m = tickets
					.stream().collect(
							Collectors.groupingBy(Ticket::getState,
									TreeMap::new, Collectors.groupingBy(
											Ticket::getSeverity, TreeMap::new,
											Collectors.counting())));
			return m;

		}
	}

	public SortedMap<Ticket.Severity, Set<Ticket>> countBySeverityOfState() {
		// return
		// tickets.stream().collect(Collectors.groupingBy(Ticket::getSeverity,
		// TreeMap::new, Collectors.toList()));
		// SortedMap<Ticket.Severity, Set<Ticket>> m = tickets.stream().collect(
		// Collectors.groupingBy(Ticket::getSeverity, TreeMap::new,
		// Collectors.toCollection(TreeSet::new)));
		SortedMap<Ticket.Severity, Set<Ticket>> m = tickets.stream().collect(
				Collectors.groupingBy(Ticket::getSeverity, TreeMap::new,
						Collectors.toSet()));
		return m;
	}

	/*
	 * Il metodo topMaintainers() dà una lista di stringhe: ogni stringa ha il
	 * formato "username:###" dove username è il nome dell'utente e ### è il
	 * numero di ticket chiusi dall'utente come maintainer. La lista è ordinata
	 * per numero decrescente di ticket e poi per username (in ordine
	 * alfabetico).
	 */
	public List<String> topMaintainers() {

		List<String> l = tickets
				.stream()
				.map(Ticket::getMaintainer)
				.filter(m -> m != null)
				.collect(
						Collectors.groupingBy(User::getUserName, TreeMap::new,
								Collectors.counting()))
				.entrySet()
				.stream()
				.sorted(Comparator
						.<Entry<String, Long>, Long> comparing(Entry::getValue,
								Comparator.reverseOrder())
						.thenComparing(Entry::getKey, Comparator.naturalOrder()))
				.map(es -> es.getKey().toString() + ":"
						+ es.getValue().toString())
				.collect(Collectors.toList());
		System.out.println(l);

		return l;

	}
}
