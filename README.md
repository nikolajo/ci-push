ci-push
=======

Project dedicated to annihilating CI polling

The idea is to create a loosely coupled system that allows different VCS's to emit commit events to different CI platforms.
Thereby achieving push from the VCS to the CI platform.


3 tiers are created:

VCS tier:
- A hook or trigger in the VCS system that detects the commit event and pushes it to the ESB

ESB tier:
- An ESB that routes the incoming events to relevant listeners

CI tier:
- A CI plugin that is able to register as a listener on the ESB and when notified trigger a build

The glue that binds these tiers is the event. The event has at least two attributes: branch and path

