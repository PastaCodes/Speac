package org.speac.core.types.internal;

import org.speac.core.types.language.*;
import org.speac.utilities.FixedList;
import org.speac.utilities.Reference;

import java.util.ArrayList;

public class Scope {
	public ArrayList<?> openedModules; // TODO check if this is left over from a previous version
	public ArrayList<Function> functions;
	public ArrayList<Variable> variables;
	public ArrayList<SystemVariable<? extends Data<?>>> systemVariables;

	public Scope() {
		this.openedModules = new ArrayList<>();
		this.functions = new ArrayList<>();
		this.variables = new ArrayList<>();
		this.systemVariables = new ArrayList<>();
	}

	public static void merge(Scope destination, Scope source) {
		for (Function function : source.functions)
			destination.addFunction(function);
		for (Variable variable : source.variables)
			destination.addVariable(variable);
		for (SystemVariable<? extends Data<?>> variable : source.systemVariables)
			destination.addSystemVariable(variable);
	}

	public Scope hereditate() {
		Scope heir = new Scope();
		Scope.merge(heir, this);
		return heir;
	}

	/**
	 * Adds the function after deleting all other functions that match any of the new function's aliases
	 * If exactly one other function was removed, then its aliases will be added to the new one
	 */
	public void addFunction(Function function) {
		boolean moreThanOne = false;
		Function toBeMerged = null;

		for (FunctionAlias alias : function.aliases) {
			Function removed = this.removeFunction(alias.name);
			if (removed != null)
				if (toBeMerged != null)
					moreThanOne = true;
				else
					toBeMerged = removed;
		}

		if (toBeMerged != null && !moreThanOne)
			function.mergeAliases(toBeMerged);

		this.functions.add(function);
	}

	/**
	 * Same spiel as in {@link #addFunction(Function)}
	 */
	public void addVariable(Variable variable) {
		boolean moreThanOne = false;
		Variable toBeMerged = null;

		for (TokenizedName name : variable.names) {
			Variable removed = this.removeVariable(name);
			if (removed != null)
				if (toBeMerged != null)
					moreThanOne = true;
				else
					toBeMerged = removed;
		}

		if (toBeMerged != null && !moreThanOne)
			variable.mergeNames(toBeMerged);

		this.variables.add(variable);


		for (TokenizedName name : variable.names)
			this.removeVariable(name);
		this.variables.add(variable);
	}

	public void addSystemVariable(SystemVariable<? extends Data<?>> variable) {
		this.removeSystemVariable(variable.identifier());
		this.systemVariables.add(variable);
	}

	// Returns the function if it was found or null otherwise
	private Function removeFunction(Prototype prototype) {
		for (Function function : this.functions)
			if (function.hasAlias(prototype, new Reference<>())) {
				this.functions.remove(function);
				return function;
			}
		return null; // Not found
	}

	// Returns the variable if it was found or null otherwise
	private Variable removeVariable(TokenizedName name) {
		for (Variable variable : this.variables)
			if (variable.hasName(name)) {
				this.variables.remove(variable);
				return variable;
			}
		return null; // Not found
	}

	// Returns the variable if it was found or null otherwise
	@SuppressWarnings("unchecked") // Same reasoning as in fetchSystemVariable
	private <T extends Data<?>> SystemVariable<T> removeSystemVariable(SystemVariable.Identifier<T> identifier) {
		for (SystemVariable<? extends Data<?>> variable : this.systemVariables)
			if (variable.is(identifier)) {
				this.systemVariables.remove(variable);
				return (SystemVariable<T>) variable;
			}
		return null; // Not found
	}

	public Function fetchFunction(Prototype prototype, Reference<FixedList<Integer>> argumentsCorrespondence) {
		for (Function function : this.functions)
			if (function.hasAlias(prototype, argumentsCorrespondence))
				return function;
		return null; // Not found
	}

	public Variable fetchVariable(TokenizedName name) {
		for (Variable variable : this.variables)
			if (variable.hasName(name))
				return variable;
		return null; // Not found
	}

	@SuppressWarnings("unchecked") // If the variable has the correct identifier than it must be safe to cast it to the known type
	public <T extends Data<?>> SystemVariable<T> fetchSystemVariable(SystemVariable.Identifier<T> identifier) {
		for (SystemVariable<? extends Data<?>> variable : this.systemVariables)
			if (variable.is(identifier))
				return (SystemVariable<T>) variable;
		return null;
	}

	public Data<?> accessVariable(TokenizedName name) {
		Variable variable = this.fetchVariable(name);
		return variable == null ? null : variable.contents;
	}

	public <T extends Data<?>> T accessSystemVariable(SystemVariable.Identifier<T> identifier) {
		SystemVariable<T> variable = this.fetchSystemVariable(identifier);
		return variable == null ? null : variable.value();
	}
}
