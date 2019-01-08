help.describe.params := ${if $(plugin),-Dplugin=$(plugin),-Dcmd=${if $(cmd),$(cmd),compile}}
help.describe.detail := ${if ${filter false no,$(detail)},,-Ddetail=true}

TARGETS := \
	all compile \
    install-top-only \
    show-dependency-tree \
    show-dependency-list \
    show-dependencies \

.PHONY: $(TARGETS)

all:: compile

test package deploy::
	mvn $(@)

compile::
	mvn test-compile

clean::
	find . -type d -name target -prune | xargs -t -n1 rm -rf

install::
	mvn install -N

install-all::
	mvn install

show-dependencies:: show-dependency-list

show-dependency-list::
	mvn dependency:list

show-dependency-tree::
	mvn dependency:tree

help::
	mvn help:help

epom::
	mvn help:effective-pom

describe::
	mvn help:describe $(help.describe.params) $(help.describe.detail)

list-plugins::
	mvn dependency:resolve-plugins | grep runtime | sort -u

describe-resolve-plugins::
	mvn help:describe -Dcmd=dependency:resolve-plugins -Ddetail=true
