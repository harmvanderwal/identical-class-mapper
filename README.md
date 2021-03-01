# Identical Class Mapper

This tool adds support for mapping identical classes on each other.
It's made as a PoC for recursive mapping of classes using generics and reflection.

# Usage:

Implementing this is quite easy. Add the project to your project and start mapping e.g.:

``DestClass destclass = IdenticalClassMapper.map(sourceInstance, DestClass.class);``

Currently only List, Set and Map are supported for recursion. No functional interface support for the time being.

No fancy frameworks or bean instantiations required. The tool is fully stateless as is.
