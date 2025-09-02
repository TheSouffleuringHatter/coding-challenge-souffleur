[![Conventional Commits](https://img.shields.io/badge/Conventional%20Commits-1.0.0-pink.svg)](https://conventionalcommits.org)
[![Renovate enabled](https://img.shields.io/badge/renovate-enabled-brightgreen.svg)](https://renovatebot.com/)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=coding-challenge-souffleur&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=coding-challenge-souffleur)
[![Test](https://github.com/TheSouffleuringHatter/coding-challenge-souffleur/actions/workflows/test.yml/badge.svg)](https://github.com/TheSouffleuringHatter/coding-challenge-souffleur/actions/workflows/test.yml)

# Coding Challenge Souffleur

A stealthy AI-powered coding assistant that whispers solutions directly into your workflow. This
Windows desktop application creates a transparent overlay that analyzes coding problems and provides
intelligent solutions without disrupting your screen sharing or recording sessions.

| What Screenshare sees                                      | What you see                                      |
|------------------------------------------------------------|---------------------------------------------------|
| <img src="./docs/what-screenshare-sees.png" width="300" /> | <img src="./docs/what-you-see.gif" width="300" /> |

[![Project Status](https://img.shields.io/badge/Project%20Status-Alpha-red.svg)](https://github.com/SchulteDev/ConversationalAI4J)

This project is in an early alpha stage. Features are functional but may contain bugs and APIs may
change.

[![Project Status](https://img.shields.io/badge/Project%20Status-Alpha-red.svg)](https://github.com/SchulteDev/ConversationalAI4J)

## ✨ The Souffleur's Magic Tricks

**🫥 Invisible to Screen Sharing**: The overlay automatically excludes itself from screen captures
and recordings — perfect for interviews, presentations, or streaming where you want AI assistance
without anyone knowing.

**👻 Ghost Mode**: Transparent overlay that appears over any application without interfering with the
underlying interface.

**🎯 Keyboard Event Absorption**: Uses native Windows keyboard hooks to "suck up" all keyboard events
within the application, preventing keyloggers or screen readers from detecting your interactions
with the AI.

## 🚀 Quick Start

### Prerequisites

- Windows 10/11
- Java 21+
- Anthropic API key with Claude access

### Setup

1. **Get your API key**: Sign up at [Anthropic](https://console.anthropic.com/)
   and create an API key
2. **Set your API key** as an environment variable:
   ```cmd
   set ANTHROPIC_API_KEY=your-api-key-here
   ```
3. **Download the latest release**
   ```bash
   git clone https://github.com/yourusername/coding-challenge-souffleur.git
   cd coding-challenge-souffleur
   ```
4. **Run the application**:

- Gradle run in Command Prompt: `gradlew.bat run`
- Silent run in PowerShell: `.\RunSilently.vbs`

## 🎮 Usage

The Souffleur operates entirely through keyboard shortcuts.

Souffleur activation modifier is `Control on the righten side`.

| Key    | Action        | Description                        |
|--------|---------------|------------------------------------|
| `R`    | 📸 Screenshot | Capture and preview current screen |
| `T`    | 🔍 Analyze    | Send screenshot to AI for analysis |
| `↑↓←→` | Move          | Reposition the overlay window      |
| `C/3`  | ⬆️⬇️ Scroll   | Navigate through AI response       |
| `W`    | 🚫 Hide/Show  | Toggle overlay visibility          |
| `Q`    | 🚫 Exit       | Exit Souffleur                     |

### Typical Workflow

1. Position the overlay over your coding problem
2. Press `R` to take a screenshot
3. Press `T` to get AI analysis
4. Use `3/C` to scroll through the detailed response
5. Press `W` to hide/show
6. Press `Q` to hide/show when done

## 🛠 Configuration

Create `.env` or edit `src/main/resources/META-INF/microprofile-config.properties`:

```properties
anthropic.model=claude-sonnet-4-0
anthropic.api.key=your-api-key-here
save.screenshot.to.file=false
```

## 📋 System Requirements

- **OS**: Windows 10 or Windows 11
- **Java**: Version 21 or higher
- **Network**: Internet connection for AI analysis

## ❓ FAQ

### How does screen hiding work?

The Souffleur calls Windows API `SetWindowDisplayAffinity` with `WDA_EXCLUDEFROMCAPTURE` flag on
the application window.

This tells Windows to exclude the window from any screen capture, recording, or
sharing — including OBS, Teams, Zoom, and Windows built-in screenshot tools.

You can **test** this functionality yourself locally by running Souffleur,
then running a screen capture tool like
[Snipping Tool](https://www.microsoft.com/de-de/windows/tips/snipping-tool).

Windows details:

- https://learn.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-setwindowdisplayaffinity

Implemented in:
[WindowFromScreenCaptureHider.java](src/main/java/dev/coding_challenge_souffleur/view/WindowFromScreenCaptureHider.java)

### How does keyboard event absorption work?

Uses low-level Windows keyboard hooks (`SetWindowsHookEx` with `WH_KEYBOARD_LL`) to intercept
system-wide keyboard events.

When a registered key combination is detected, the application
processes the action and marks the event as "handled," preventing it from reaching other
applications or being logged by external monitoring tools.

Windows details:

- https://learn.microsoft.com/en-us/windows/win32/winmsg/lowlevelkeyboardproc
- https://learn.microsoft.com/en-us/windows/win32/winmsg/about-hooks#wh_keyboard_ll

Implemented in:
[KeyboardEventProcessor.java](src/main/java/windowskeyboardhook/KeyboardEventProcessor.java)

## 🔧 Development

### Building and Testing

- **Build**: `./gradlew build` or `gradlew.bat build`
- **Run tests**: `./gradlew test`
- **Run single test**: `./gradlew test --tests "ClassName.methodName"`
- **Generate coverage**: `./gradlew jacocoTestReport`

### Architecture Overview

This is a Windows-only JavaFX application with:

- **CDI (Weld)** dependency injection
- **JNA** for Windows API integration (`SetWindowDisplayAffinity`, keyboard hooks)
- **Anthropic Java SDK** for Claude API
- **TestFX + Mockito** for testing

Main entry: `ApplicationLauncher.java` → `JavaFxApplication.java`

Key packages: `model/` (services), `view/` (JavaFX), `windowskeyboardhook/` (Windows integration)

## 💡 Inspired by...

1. Interview with **Chungin Lee**, author of [Interview Coder](https://www.interviewcoder.co):
   > Someone could probably build a working prototype version of this that works in less than 1,000
   > lines of code.
   > 
   > https://gizmodo.com/a-student-used-ai-to-beat-amazons-brutal-technical-interview-he-got-an-offer-and-someone-tattled-to-his-university-2000571562
   
   Yeah, he was right (976 executables lines of code).

2. > A simple key stroke capturing react app can identify when users are pressing unnecessary keys 
   > during any rounds.
   > 
   > https://www.reddit.com/r/brdev/comments/1jlt9x5/comment/modns5c  
   
   Ha, wrong!
