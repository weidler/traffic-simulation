package experiment;

import org.apache.commons.math3.analysis.function.Exp;

import java.util.ArrayList;

public class ExperimentWrapper {

	ArrayList<Experiment> experiments;
	ArrayList<Experiment> finished_experiments;

	public ExperimentWrapper() {
		this.experiments = new ArrayList<>();
		this.finished_experiments = new ArrayList<>();
	}

	public Experiment finishExperiment(Experiment exp) {
		this.experiments.remove(exp);
		System.out.println(this.experiments.size());
		this.finished_experiments.add(exp);

		return exp;
	}

	public Experiment currentExperiment() {
		if (!isAllFinished()) {
			return this.experiments.get(0);
		}

		return null;
	}

	public Experiment nextExperiment() {
		return this.experiments.get(1);
	}

	public Experiment addExperiment(Experiment exp) {
		this.experiments.add(exp);
		return exp;
	}

	public Experiment removeExperiment(Experiment exp) {
		if (this.experiments.contains(exp)) this.experiments.remove(exp);
		return exp;
	}

	public boolean isAllFinished() {
		return this.experiments.size() == 0;
	}

	public ArrayList<Experiment> getAllExperiments() {
		ArrayList<Experiment> all = ((ArrayList<Experiment>) this.finished_experiments.clone());
		all.addAll(this.experiments);

		return all;
	}

	public boolean isFinished(Experiment exp) {
		return this.finished_experiments.contains(exp);
	}

	public void saveAll() {
		for (Experiment exp : finished_experiments) {
			exp.save();
		}
	}

	public void createFinalReport() {

	}

	public Integer currentExperimentID() {
		return finished_experiments.size() + 1;
	}

	public int remainingExperiments() {
		return this.experiments.size();
	}

	public ArrayList<Experiment> getRemainingExperiments() {
		return this.experiments;
	}
}
