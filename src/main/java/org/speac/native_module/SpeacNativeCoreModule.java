package org.speac.native_module;

import org.speac.core.SpeacCoreModuleUtilities;
import org.speac.core.types.internal.Scope;
import org.speac.core.types.language.CoreModule;
import org.speac.data_types.*;
import org.speac.utilities.FixedList;

public class SpeacNativeCoreModule implements CoreModule {
	@Override public void load(Scope scope) {
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "rest",					Miscellaneous.REST          	);
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "learn <",				Miscellaneous.LEARN         	);
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "run <",					Miscellaneous.RUN           	);
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "run < > here",			Miscellaneous.RUN_HERE      	);
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "repeat <",				Miscellaneous.REPEAT        	);
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "break < > times",		Miscellaneous.BREAK_TIMES   	);
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "raise error < > on <",	Miscellaneous.RAISE_ERROR_ON	);

		SpeacCoreModuleUtilities.defineCoreFunction(scope, "define < ><",           FunctionsHandling.DEFINE        );
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "return <",              FunctionsHandling.RETURN        );
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "add alias < > to <",    FunctionsHandling.ADD_ALIAS_TO  );

		SpeacCoreModuleUtilities.defineCoreFunction(scope, "create < > as <",   VariablesHandling.CREATE_AS     );
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "set < > to <",      VariablesHandling.SET_TO        );
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "value of <",        VariablesHandling.VALUE_OF      );
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "type of <",         VariablesHandling.TYPE_OF       );
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "lend <",            VariablesHandling.LEND          );
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "borrow <",          VariablesHandling.BORROW        );
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "add name < > to <", VariablesHandling.ADD_NAME_TO   );

		SpeacCoreModuleUtilities.defineCoreFunction(scope, "if < ><",       Conditionals.IF         );
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "else if < ><",  Conditionals.ELSE_IF    );
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "else <",        Conditionals.ELSE       );

		SpeacCoreModuleUtilities.defineVariadicCoreFunction(scope, "say",   IOHandling.SAY);
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "ask <",         IOHandling.ASK);

		SpeacCoreModuleUtilities.defineCoreFunction(scope, "> as string",	Conversions.AS_STRING	);
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "> as number",	Conversions.AS_NUMBER	);

		SpeacCoreModuleUtilities.defineCoreFunction(scope, "> equals <",            Maths.EQUALS            );
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "> plus <",              Maths.PLUS              );
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "> minus <",             Maths.MINUS             );
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "> times <",             Maths.TIMES             );
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "> over <",              Maths.OVER              );
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "quotient of < > over <", Maths.QUOTIENT_OF_OVER);
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "remainder of < > over <", Maths.REMAINDER_OF_OVER);
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "> to the <",            Maths.TO_THE            );
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "absolute value of <",	Maths.ABSOLUTE_VALUE_OF	);
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "> is greater than <",   Maths.IS_GREATER_THAN   );
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "> is less than <",      Maths.IS_LESS_THAN      );
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "> and <",               Maths.AND               );
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "> or <",                Maths.OR                );
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "not <",                 Maths.NOT               );
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "> trimmed to < > decimal places", Maths.TRIMMED_TO_DECIMAL_PLACES);
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "> rounded to < > decimal places", Maths.ROUNDED_TO_DECIMAL_PLACES);
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "> trimmed",				Maths.TRIMMED);
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "> rounded",				Maths.ROUNDED);

		SpeacCoreModuleUtilities.defineVariadicCoreFunction(scope, "list with",         ListsHandling.LIST_WITH         );
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "length of <",               ListsHandling.LENGTH_OF         );
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "item < > of <",             ListsHandling.ITEM_OF           );
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "set item < > of < > to <",  ListsHandling.SET_ITEM_OF_TO    );
		SpeacCoreModuleUtilities.defineCoreFunction(scope, "append < > to <",           ListsHandling.APPEND_TO         );

		SpeacCoreModuleUtilities.defineVariable(scope, "True",          			new SpeacBoolean(true)      				);
		SpeacCoreModuleUtilities.defineVariable(scope, "False",         			new SpeacBoolean(false)     				);
		SpeacCoreModuleUtilities.defineVariable(scope, "Empty",         			new SpeacEmpty()            				);
		SpeacCoreModuleUtilities.defineVariable(scope, "Answer",        			new SpeacEmpty()            				);
		SpeacCoreModuleUtilities.defineVariable(scope, "Empty List",    			new SpeacList(new FixedList<>())			);
		SpeacCoreModuleUtilities.defineVariable(scope, "Invalid Argument Type",		new SpeacString("Invalid Argument Type")	);
		SpeacCoreModuleUtilities.defineVariable(scope, "Invalid Argument",			new SpeacString("Invalid Argument") 		);
		SpeacCoreModuleUtilities.defineVariable(scope, "Invalid Referenced Type",	new SpeacString("Invalid Referenced Type")	);
		SpeacCoreModuleUtilities.defineVariable(scope, "Meaning Of Life",			new SpeacInteger(42)						);
	}
}
