package tools.constellation;

import tools.simulation.assets.entities.Satellite;
import tools.simulation.structures.Event;
import tools.simulation.structures.Interval;
import tools.simulation.utils.Utils;
import tools.simulation.Simulation;
import tools.simulation.assets.entities.Device;

import java.util.*;
import java.util.stream.Collectors;

public class Constellation {

    private final Simulation simulation = new Simulation();
    private List<Device> devices;
    private List<Satellite> satellites;
    private List<Interval> currentIntervals = new ArrayList<>();
    private List<Interval> combinedIntervals = new ArrayList<>();
    private List<Interval> allAccesses = new ArrayList<>();
    private List<Event> eventsList = new ArrayList<>();
    private List<Event> allEvents = new ArrayList<>();
    private boolean includeCoverageGaps = false;
    private int povOption = 0;
    private double maxMCG = Double.MAX_VALUE;
    private long lastSimTime = 0;
    private boolean debugMode = false;

    public Constellation() {

    }

    public Constellation(String dateStart, String dateEnd, double step, double visibilityThreshold) {
        this.simulation.setParams(dateStart, dateEnd, step, visibilityThreshold);
    }

    public Constellation(List<Device> gateways, List<Satellite> satellites) {
        this.devices = gateways;
        this.satellites = satellites;
    }

    public Constellation(String dateStart, String dateEnd, List<Device> gateways, List<Satellite> satellites, double step, double visibilityThreshold) {
        this.devices = gateways;
        this.satellites = satellites;
        this.simulation.setParams(dateStart, dateEnd, step, visibilityThreshold);
    }

    public Constellation(String dateStart, String dateEnd, String gatewaysFile, String satellitesFile, double step, double visibilityThreshold) {
        this(dateStart, dateEnd, Utils.devicesFromFile(gatewaysFile), Utils.satellitesFromFile(satellitesFile), step, visibilityThreshold);
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public void setPovOption(int povOption) {
        this.povOption = povOption >= 1 ? 1 : 0;
    }

    public void setIncludeCoverageGaps(boolean includeCoverageGaps) {
        this.includeCoverageGaps = includeCoverageGaps;
    }

    public void setCurrentIntervals(List<Interval> currentIntervals) {
        this.currentIntervals = currentIntervals;
    }

    public void setAssets(List<Device> gateways, List<Satellite> satellites) {
        this.devices = gateways;
        this.satellites = satellites;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public void setSatellites(List<Satellite> satellites) {
        this.satellites = satellites;
    }

    public void addDevice(Device device) {
        devices.add(device);
    }

    public void addSatellite(Satellite satellite) {
        satellites.add(satellite);
    }

    public void setScenarioParams(String start, String end, double step, double th) {
        simulation.setParams(start, end, step, th);
    }

    public void setScenarioTimeSpan(long startTime, long endTime) {
        simulation.setStartTime(startTime);
        simulation.setEndTime(endTime);
    }

    public List<Interval> getCombinedIntervals() {
        return combinedIntervals;
    }

    public List<Interval> getAllAccesses() {
        return allAccesses;
    }

    public List<Interval> getCurrentIntervals() {
        return currentIntervals;
    }

    public double getMaxMCG() {
        return this.maxMCG;
    }

    public double getMaxMCGMinutes() {
        return this.maxMCG / (60.0 * 1000.0);
    }

    public long getLastSimTime() {
        return this.lastSimTime;
    }

    public void computeDevicesPOV() {

        long t0 = System.currentTimeMillis();

        setPovOption(0);

        allAccesses.clear();
        currentIntervals.clear();
        allEvents.clear();

        if (devices.isEmpty() || satellites.isEmpty()) {
            System.out.println("Check assets!");
            return;
        }

        for (Device device : devices) {

            currentIntervals.clear();

            for (Satellite satellite : satellites) {

                if (debugMode) System.out.println("Computing " + device.getId() + " -> " + satellite.getId());

                simulation.setAssets(device, satellite);
                simulation.computeAccess();
                synchronized (simulation) {
                    if (simulation.getIntervals().isEmpty()) {
//                        allAccesses.add(new Interval(Utils.stamp2unix(simulation.getStartTime()), Utils.stamp2unix(simulation.getEndTime()),
//                                new ArrayList<>(device.getId()), new ArrayList<>()));
                    } else {
                        currentIntervals.addAll(simulation.getIntervals());
                    }
                }
            }

            currentIntervals.sort((i1, i2) -> (int) (i1.getStart() - i2.getStart()));

            allAccesses.addAll(computeDevices2Constellation());

        }

        lastSimTime = System.currentTimeMillis() - t0;

    }

    public void computeSatellitesPOV() {

        setPovOption(1);

        allAccesses.clear();
        currentIntervals.clear();
        allEvents.clear();

        if (devices.isEmpty() || satellites.isEmpty()) {
            System.out.println("Check assets!");
            return;
        }

        for (Satellite satellite : satellites) {

            currentIntervals.clear();

            for (Device device : devices) {

                if (debugMode) System.out.println("Computing " + device.getId() + " -> " + satellite.getId());

                simulation.setAssets(device, satellite);
                simulation.computeAccess();
                synchronized (simulation) {
                    if (simulation.getIntervals().isEmpty()) {
//                        allAccesses.add(new Interval(Utils.stamp2unix(simulation.getStartTime()), Utils.stamp2unix(simulation.getEndTime()),
//                                new ArrayList<>(device.getId()), new ArrayList<>()));
                    } else {
                        currentIntervals.addAll(simulation.getIntervals());
                    }
                }
            }

            currentIntervals.sort((i1, i2) -> (int) (i1.getStart() - i2.getStart()));

            allAccesses.addAll(computeConstellation2Devices());

        }
    }

    /**
     *
     **/
    private List<Interval> computeConstellation2Devices() {

        if (currentIntervals.size() <= 1) {
            return currentIntervals;
        }

        combinedIntervals.clear();
        Set<Integer> inContact = new LinkedHashSet<>();
        List<Event> eventList = intervals2eventsSatPOV(currentIntervals);
        eventList = eventList.subList(1, eventList.size());

        var currentInterval = currentIntervals.get(0);
        inContact.add(currentInterval.getFirstFrom());

        for (Event event : eventList) {
            if (!inContact.contains(event.getWho())) {  // If I establish contact with a new asset
                inContact.add(event.getWho());
                if (includeCoverageGaps || !currentInterval.getFromAssets().isEmpty()) {
                    combinedIntervals.add(new Interval(currentInterval.getStart(), event.getTime(), currentInterval.getFromAssets(), currentInterval.getToAssets()));
                }
                currentInterval.addFrom(event.getWho());
            } else {    // If the asset is already in contact
                inContact.remove(event.getWho());
                combinedIntervals.add(new Interval(currentInterval.getStart(), event.getTime(), currentInterval.getFromAssets(), currentInterval.getToAssets()));
                currentInterval.removeFrom(event.getWho());
            }
            currentInterval.setStart(event.getTime());
        }

        return combinedIntervals;

    }

    /**
     *
     **/
    public List<Interval> computeDevices2Constellation() {

        if (currentIntervals.size() <= 1) {
            return currentIntervals;
        }

        combinedIntervals.clear();
        Set<Integer> inContact = new LinkedHashSet<>();
        List<Event> eventList = intervals2eventsDevicePOV(currentIntervals);
        eventList = eventList.subList(1, eventList.size());

        var currentInterval = currentIntervals.get(0);
        inContact.add(currentInterval.getFirstTo());

        for (Event event : eventList) {
            if (!inContact.contains(event.getWho())) {  // If I establish contact with a new asset
                inContact.add(event.getWho());
                if (includeCoverageGaps || !currentInterval.getToAssets().isEmpty()) {
                    combinedIntervals.add(new Interval(currentInterval.getStart(), event.getTime(), currentInterval.getFromAssets(), currentInterval.getToAssets()));
                }
                currentInterval.addTo(event.getWho());
            } else {    // If the asset is already in contact
                inContact.remove(event.getWho());
                combinedIntervals.add(new Interval(currentInterval.getStart(), event.getTime(), currentInterval.getFromAssets(), currentInterval.getToAssets()));
                currentInterval.removeTo(event.getWho());
            }
            currentInterval.setStart(event.getTime());
        }

        return combinedIntervals;

    }

    private List<Event> intervals2events(List<Interval> intervals) {

        List<Event> eventsList = new ArrayList<>();

        switch (povOption) {
            case 1:
                for (Interval interval : intervals) {
                    eventsList.add(new Event(interval.getStart(), interval.getFirstTo()));
                    eventsList.add(new Event(interval.getEnd(), interval.getFirstTo()));
                }
                break;
            case 0:
            default:
                for (Interval interval : intervals) {
                    eventsList.add(new Event(interval.getStart(), interval.getFirstFrom()));
                    eventsList.add(new Event(interval.getEnd(), interval.getFirstFrom()));
                }
                break;
        }

        eventsList.sort((e1, e2) -> (int) (e1.getTime() - e2.getTime()));
        return eventsList;

    }

    private List<Event> intervals2eventsSatPOV(List<Interval> intervals) {

        eventsList.clear();

        for (Interval interval : intervals) {
            eventsList.add(new Event("access_gained", interval.getStart(), interval.getFirstFrom(), interval.getFirstTo()));
            eventsList.add(new Event("access_lost", interval.getEnd(), interval.getFirstFrom(), interval.getFirstTo()));
        }

        allEvents.addAll(eventsList);
        eventsList.sort((e1, e2) -> (int) (e1.getTime() - e2.getTime()));
        return eventsList;

    }

    private List<Event> intervals2eventsDevicePOV(List<Interval> intervals) {

        eventsList.clear();

        for (Interval interval : intervals) {
            eventsList.add(new Event("access_gained", interval.getStart(), interval.getFirstTo(), interval.getFirstFrom()));
            eventsList.add(new Event("access_lost", interval.getEnd(), interval.getFirstTo(), interval.getFirstFrom()));
        }

        allEvents.addAll(eventsList);
        eventsList.sort((e1, e2) -> (int) (e1.getTime() - e2.getTime()));
        return eventsList;

    }

    /**
     * Gets the last List<Event> eventList computed
     **/
    public List<Event> getEventsList() {
        return eventsList;
    }

    /**
     * Gets the last List<Event> allEvents computed
     **/
    public List<Event> getAllEventsList() {
        return allEvents;
    }

    /**
     * Gets the last List<Event> eventList computed
     **/
    public List<Event> getAllEventsOrdered() {
        allEvents.sort((e1, e2) -> (int) (e1.getTime() - e2.getTime()));
        return allEvents;
    }

    /**
     * Returns a new filtered List containing only intervals that include contacts from/to at least N devices to
     * a single gateway
     **/
    public List<Interval> filterAtLeastNDevices(List<Interval> list, int n) {

        switch (povOption) {
            case 0:
                return list.stream().filter(interval -> interval.getToAssets().size() >= n).collect(Collectors.toList());
            case 1:
            default:
                return list.stream().filter(interval -> interval.getFromAssets().size() >= n).collect(Collectors.toList());
        }

    }

    /**
     * Returns a new filtered List containing only intervals that include contacts from/to at least N devices to
     * a single gateway
     **/
    public List<Interval> filterGetGaps(List<Interval> list) {

        switch (povOption) {
            case 0:
                return list.stream().filter(interval -> interval.getToAssets().isEmpty()).collect(Collectors.toList());
            case 1:
            default:
                return list.stream().filter(interval -> interval.getFromAssets().isEmpty()).collect(Collectors.toList());
        }

    }

    /**
     * computes the Maximum Coverage Gap for the computed accesses, unless they were not computed
     **/
    public void computeMaxMCG() {
        computeMaxMCG(allAccesses);
    }

    /**
     * Computes the Maximum Coverage Gap for a given list (MCG) in milliseconds
     **/
    public void computeMaxMCG(List<Interval> list) {

        Interval maxMCGInterval = new Interval(simulation.getStartTimeUnix(), simulation.getEndTimeUnix());
        try {
            switch (povOption) {
                case 0:
                    maxMCGInterval = Collections.max(list.stream().filter(interval -> interval.getToAssets().isEmpty()).collect(Collectors.toList()),
                            (d1, d2) -> (int) (d1.getDuration() - d2.getDuration()));
                    break;
                case 1:
                default:
                    maxMCGInterval = Collections.max(list.stream().filter(interval -> interval.getFromAssets().isEmpty()).collect(Collectors.toList()),
                            (d1, d2) -> (int) (d1.getDuration() - d2.getDuration()));
                    break;
            }
        } catch (NoSuchElementException nse) {
            System.out.println("No such element exception");
            this.maxMCG = simulation.getTimeSpan() / (1000.0 * 60.0);
        }

        this.maxMCG = maxMCGInterval.getDuration();
    }


}
