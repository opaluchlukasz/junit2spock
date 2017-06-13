package com.github.opaluchlukasz.junit2spock.core.node;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.AstProvider;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class GroovyClosureBuilder {

    public static final String IT = "it";

    private final AstProvider astProvider;
    private final ASTNodeFactory astNodeFactory;

    @Autowired
    public GroovyClosureBuilder(AstProvider astProvider, ASTNodeFactory astNodeFactory) {
        this.astProvider = astProvider;
        this.astNodeFactory = astNodeFactory;
    }

    public Builder aClosure() {
        return new Builder(astProvider, astNodeFactory);
    }

    public static final class Builder {

        private AstProvider astProvider;
        private ASTNodeFactory astNodeFactory;
        private List<SingleVariableDeclaration> arguments = new LinkedList<>();
        private List<Statement> body = new LinkedList<>();
        private TypeLiteral typeLiteral;

        private Builder(AstProvider astProvider, ASTNodeFactory astNodeFactory) {
            this.astProvider = astProvider;
            this.astNodeFactory = astNodeFactory;
        }

        public Builder withArgument(SingleVariableDeclaration argument) {
            this.arguments.add(argument);
            return this;
        }

        public Builder withBodyStatements(List<Statement> bodyStatements) {
            this.body.addAll(bodyStatements);
            return this;
        }

        public Builder withBodyStatement(Statement statement) {
            this.body.add(statement);
            return this;
        }

        public Builder withTypeLiteral(TypeLiteral typeLiteral) {
            this.typeLiteral = typeLiteral;
            return this;
        }

        public GroovyClosure build() {
            return new GroovyClosure(astNodeFactory, astProvider, body, typeLiteral, arguments);
        }
    }
}
