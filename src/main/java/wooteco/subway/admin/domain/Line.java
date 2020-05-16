package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

public class Line {
	private static final int ONE = 1;
	private static final int FIRST_INDEX = 0;

	@Id
	private final Long id;
	private final String name;
	private final LocalTime startTime;
	private final LocalTime endTime;
	private final int intervalTime;
	private final String color;
	@MappedCollection(idColumn = "line_id", keyColumn = "sequence")
	private final List<Edge> edges;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	Line(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime, final String color,
		List<Edge> edges, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.id = id;
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.intervalTime = intervalTime;
		this.color = color;
		this.edges = edges;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public static Line of(String name, LocalTime startTime, LocalTime endTime, int intervalTime
		, String color) {
		LocalDateTime localDateTime = LocalDateTime.now();
		return new Line(null, name, startTime, endTime, intervalTime, color, new LinkedList<>(),
			localDateTime, localDateTime);
	}

	public Line withId(Long id) {
		return new Line(id, this.name, this.startTime, this.endTime, this.intervalTime, this.color,
			this.edges, this.createdAt, this.updatedAt);
	}

	public Line update(Line line) {
		return new Line(this.id, line.name, line.startTime, line.endTime, line.intervalTime, line.color,
			line.edges, this.createdAt, line.updatedAt);
	}

	public void addEdge(Edge edge) {
		if (edges.isEmpty() && edge.isNotStartEdge()) {
			edges.add(Edge.starter(edge.getPreStationId()));
			edges.add(edge);
			return;
		}

		int index = findIndex(edge);
		edges.add(index, edge);

		if (index < getEndIndexOfEdges()) {
			edges.get(index + ONE).updatePreStationId(edge.getStationId());
		}
	}

	private int findIndex(Edge edge) {
		if (edge.isStartEdge()) {
			return 0;
		}
		return edges.stream()
			.filter(item -> item.equalsStationId(edge.getPreStationId()))
			.findFirst()
			.map(item -> edges.indexOf(item) + ONE)
			.orElseThrow(IllegalArgumentException::new);
	}

	private int getEndIndexOfEdges() {
		return edges.size() - 1;
	}

	public void removeEdgeById(Long stationId) {
		int index = edges.stream()
			.filter(edge -> edge.equalsStationId(stationId))
			.findFirst()
			.map(edges::indexOf)
			.orElseThrow(() -> new IllegalArgumentException("지우려는 역이 존재하지 않습니다."));

		Edge removeEdge = edges.remove(index);

		if (edges.isEmpty()) {
			return;
		}

		if (removeEdge.isStartEdge()) {
			Edge newFirstEdge = edges.get(FIRST_INDEX);
			newFirstEdge.updatePreStationId(newFirstEdge.getStationId());
			return;
		}

		if (index < edges.size() - ONE) {
			edges.get(index).updatePreStationId(edges.get(index - ONE).getStationId());
		}
	}

	public List<Long> getEdgesId() {
		return edges.stream()
			.map(Edge::getStationId)
			.collect(Collectors.toList());
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public int getIntervalTime() {
		return intervalTime;
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public String getColor() {
		return color;
	}

	@Override
	public String toString() {
		return "Line{" +
			"id=" + id +
			", name='" + name + '\'' +
			", startTime=" + startTime +
			", endTime=" + endTime +
			", intervalTime=" + intervalTime +
			", color='" + color + '\'' +
			", edges=" + edges +
			", createdAt=" + createdAt +
			", updatedAt=" + updatedAt +
			'}';
	}
}
