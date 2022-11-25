This is a simple diagram to explain how the main repo branches relate to each other.

The master branch defines a microservice direction by putting the seed for independent service, so there is no dependency 
on code level or in build and deployment.

At this early time of the project and quick prototype is required, committing to the micorservice architecture will have
high cost that is not feasible. So, a new simpler approach was taken to move faster. connect the modules to act as one unit.

To keep the work that was already done, a new branch was created, master-monolith. It acts as the base for the new direction
in development, a monolith application. It was taken from master at the tag, 0.2. The module configuration changes were
made at the tag, mono-0.1. 
The branch, dev-monolith, will act as the dev branch for this direction.

                                    master (0.2)
                             /                       \
                           /                           \
                        dev                            master-monolith (mono-0.1)
                  (not yet exist)                            \
                    / ........ \                            dev-monolith
                feature-1     feature-n                      / ........ \
                                                 mono-feature-1         mono-feature-n