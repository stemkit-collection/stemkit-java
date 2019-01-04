
TARGETS := \
	all compile \
    install-top-only \
    show-dependency-tree \
    show-dependency-list \
    show-dependencies \

.PHONY: $(TARGETS)

all:: compile

compile::
	mvn test-compile

install-top-only::
	mvn install -N

show-dependencies:: show-dependency-list

show-dependency-list::
	mvn dependency:list

show-dependency-tree::
	mvn dependency:tree
