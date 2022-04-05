# Scalafix + GitHub Actions

## What's this?

This repository serves as a playground and as a showcase on how we can use the Scala ecosystem for new and exciting ideas. Here we show how we can use scalafix and scala.js to extend the GitHub ecosystem.

You find an example in  https://github.com/ingarabr/scalafix-gha/pull/2

## Scalafix

Scalafix has an option to report diagnostics and provide patches. Patches are useful for code migration and programmatically rewrite our codebase. One example is to remove unused imports. It also provides diagnostics messages where we can guide the user to correct code that we cannot do programmatically for various reasons. It's what many code linters do for us. Scalafix builds upon Scalameta that tools like scalafmt and metals are using. This can be pretty powerful since we have a more stable interface with our code than other linter that are often required to hook into the compiler directly. In other words, we can leverage scalafix as a linter.

This repository uses a patch version of sbt-scalafix that can extract the diagnostics messages. The messages are written to a file that we pick up later on in our build pipeline.

## Scala.js and GitHub Checks

GitHub has a concept called Checks and Check-runs. Check-runs can prove tool-assisted code reviews and checks to block a pull request to be merged without passing. Given the right amount of information, we can use the linting violation as a way to educate and prevent ourselves from introducing bad habits, potential bugs and code smell.

You'll find a GitHub action that will pick up the diagnostics messages from scalafix and creates check-run results. The action is written in scala.js and uses ScalablyTyped to leverage the existing JavaScript GitHub action ecosystem. This is mostly done for demonstration purposes. In the long term, it can be a nice way to separate the concerns that belong to the build system and the CI system and share code across them.


# Limitations, discoveries and other thoughts

## Extracting diagnostics messages and patches
I have not found a good way to get access to the patches and enough metadata around them to use them as code annotations/diagnostics messages. I'm also questioning the way I'm currently hooking into the system to get a hold of the diagnostic messages in the sbt plugins. One potential solution can be to move this concern into the code of scalafix. An output file can be created similarly to the Scalameta file. This approach will decouple us from the build tools and can open up other possibilities. These are just my thoughts and need to be discussed with the maintainers of scalafix.

Patches can be a bit too low level for what a linting tool wants to do. It can communicate what the linting rule wants to achieve but has no information about why it wants that particular change applied. I do not think it's a good idea to blindly accept linting rules without understanding why they're considered bad practices. Letting the tool educate us can also be a good way for improving the skill level of people new to the scala language.

## Supporting build tools and CI solutions

Avoid building tools that are looked to one build tool or CI solution. We should be able to use it across the main tools used by the ecosystem. If it's not supported, then we should try to facilitate a way to let them integrate with scalafix. That can be done by providing libraries to file exports. There are other code diagnostics tools and formats that we could fill the gap (surefire, junit etc).

## Improve build tool support

Scalafix and its rules are quite fragmented. Writing good rules that can be configured makes them really reusable. However, it pushed the responsibility to the ones configuring them. This can be problematic to keep in sync across multiple code bases in an organization. Having some opinionated and preconfigured setup would make it easier for the community adapting it. 
