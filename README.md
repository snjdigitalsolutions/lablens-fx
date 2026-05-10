# VISION

LabLens is a cross-platform desktop application built for homelab enthusiasts
who want a single, unified tool to track configuration changes and view logs
across all their Linux hosts. It connects via SSH, snapshots key
configuration paths on a schedule, and provides a rich log viewing 
experience all from a clean JavaFX interface backed by Spring Boot.

The core insight: when something breaks in your homelab, the first two 
questions a person asks are "what changed?" and "what do the logs say?" 
Those questions require jumping between Git repos, SSH sessions, and log 
files manually. LabLens puts both answers in one place.

## Milestones

* A user can register their homelab hosts and see which ones are reachable.
* A user can see exactly what changed in /etc/nginx.conf since last 
  Tuesday.
* A user can isolate and navigate errors in a 100k-line Spring Boot log in 
  seconds.
* A user can see that an nginx config changed at 2:14pm and 502 errors began at 
  2:15pm — without leaving the app.
