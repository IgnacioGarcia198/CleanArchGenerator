package com.garcia.ignacio.cleanarchgenerator.generator

import com.garcia.ignacio.cleanarchgenerator.ui.dartClassToFile

class TextGenerator {
    fun generateEventText(eventClass: String, events: List<String>): String {
        return """
abstract class $eventClass {
  const $eventClass();
}
    
${events.joinToString("\n\n") {
"""
class $it {
  const $it();
}
""".trimIndent()
}}
""".trimIndent()
    }

    fun generateStateText(stateClass: String, states: List<String>): String {
        return """
abstract class $stateClass {
  const $stateClass();
}
    
${
states.joinToString("\n\n") {
"""
class $it {
  const $it();
}
""".trimIndent()
}}
""".trimIndent()
    }

    fun generateBlocText(blocClass: String, eventClass: String, stateClass: String, events: List<String>): String {
        return """
import 'package:flutter_bloc/flutter_bloc.dart';
import '${eventClass.dartClassToFile()}';
import '${stateClass.dartClassToFile()}';

class $blocClass extends Bloc<$eventClass, $stateClass> {
  
  $blocClass() {
    ${
        events.joinToString("\n") { 
            "on<$it> (on$it);"
        }
    }
  }
  
  ${
        events.joinToString("\n\n") { event ->
            """
                Future<void> on$event($event event, Emitter<$stateClass> emit) async {
                    
                }
            """.trimIndent()
        }
    }
}
        """.trimIndent()

    }

    fun generateRepositoryText(repositoryClass: String): String {
        return """
abstract class $repositoryClass {
    Future<String> getString();
}
        """.trimIndent()
    }

    fun generateRepositoryImplText(repositoryImplClass: String, repositoryClass: String): String {
        return """
import '../../domain/repositories/account_auth_repository.dart';
     
class $repositoryImplClass implements $repositoryClass {
    @override
    Future<String> getString() {
        return "hello";
    }
}
        """.trimIndent()
    }

    fun generateUseCaseText(useCaseClass: String, repositoryClass: String): String {
        return """
import '../repositories/account_auth_repository.dart';
     
class $useCaseClass implements UseCase<String, void> {
  final $repositoryClass repository;

  const $useCaseClass(this.repository);
  
  @override
  Future<String> call({void params}) {
    return repository.getString();
  }
}
        """.trimIndent()
    }
}