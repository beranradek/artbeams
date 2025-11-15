# Article Editing AI Agent

## Overview

This feature implements an AI-powered assistant for article editing in the ArtBeams CMS. The agent uses OpenAI's GPT models to help authors create, edit, and improve articles written in Czech language using CommonMark markdown format.

## Features

- **Chat Interface**: Interactive AI assistant accessible from the article editor
- **Streaming Responses**: Real-time streaming of AI responses for better user experience
- **Article Context**: Agent receives the current article (title, perex, body) for context-aware assistance
- **Conversation History**: Maintains up to 20 messages in memory per session
- **Article Detection**: Automatically detects when the AI proposes a new article version (in triple backticks)
- **Diff Viewer**: Side-by-side comparison of original and AI-generated article versions
- **Easy Application**: One-click application of AI-generated content to the editor
- **Responsive Design**: Mobile-ready interface that works on all devices

## Architecture

### Backend Components

1. **ArticleEditingAgent** (`org.xbery.artbeams.articles.agent.ArticleEditingAgent`)
   - Service class that manages OpenAI API integration
   - Handles conversation history (max 20 messages per session)
   - Provides streaming responses
   - Extracts article content from AI responses
   - Configurable system prompt via database

2. **ArticleEditingAgentController** (`org.xbery.artbeams.articles.agent.ArticleEditingAgentController`)
   - REST endpoints for AJAX communication
   - Session management for conversation tracking
   - Endpoints:
     - `POST /admin/articles/agent/message` - Send message and stream response
     - `POST /admin/articles/agent/clear` - Clear conversation history
     - `GET /admin/articles/agent/chat` - Get chat interface template

### Frontend Components

1. **FreeMarker Template** (`templates/articles/agent/chat.ftl`)
   - Chat modal dialog
   - Diff viewer modal
   - Bootstrap 5 based UI

2. **JavaScript** (`static/js/article-agent.js`)
   - AJAX communication using EventSource for streaming
   - Chat message rendering
   - Diff generation and display
   - Copy to clipboard and apply to editor functionality

3. **CSS** (`static/css/article-agent.css`)
   - Responsive design (mobile-ready)
   - Chat interface styling
   - Diff viewer styling
   - Floating action button (FAB) for opening the agent

## Installation

### 1. Dependencies

The OpenAI Java SDK dependency is already added to `build.gradle`:

```groovy
implementation("com.openai:openai-java-spring-boot-starter:4.8.0")
```

Version is defined in `gradle.properties`:

```properties
openaiVersion = 4.8.0
```

### 2. Environment Configuration

Set the OpenAI API key as an environment variable:

```bash
export OPENAI_API_KEY="your-openai-api-key-here"
```

### 3. Database Configuration

Run the SQL script to add the system prompt configuration:

```bash
psql -U your_username -d your_database -f src/main/resources/sql/article_editing_agent_config.sql
```

Or manually insert into the `config` table:

```sql
INSERT INTO config (entry_key, entry_value)
VALUES ('article.editing.agent.system.prompt', 'your-custom-system-prompt-here')
ON CONFLICT (entry_key) DO UPDATE SET entry_value = EXCLUDED.entry_value;
```

## Usage

1. **Open Article Editor**: Navigate to any article edit page (`/admin/articles/{id}/edit`)

2. **Open AI Assistant**: Click the purple floating robot icon in the top-right corner

3. **Ask for Help**: Type your request in the chat interface, for example:
   - "Uprav tento článek a zlepši jeho strukturu"
   - "Přepiš perex článku tak, aby byl zajímavější"
   - "Napiš úvod k tomuto článku"

4. **Review Changes**: If the AI generates a new article version (in triple backticks), a "Zobrazit porovnání" button appears

5. **Apply Changes**:
   - Click the comparison button to open the diff viewer
   - Review the changes (left side shows diff, right side shows new version)
   - Edit the new version if needed
   - Click "Použít v editoru" to apply the changes to the article editor

6. **Continue Conversation**: The agent maintains conversation history, so you can refine the article through multiple interactions

7. **Start Fresh**: Click "Nová konverzace" to clear history and start a new conversation

## Configuration

### Model Selection

The agent uses GPT-4o by default (configured in `ArticleEditingAgent.DEFAULT_MODEL`). 
To change the model, update the constant in the service class.

### Conversation History

The agent maintains the last 20 messages (including system message) per session. This can be adjusted by changing `ArticleEditingAgent.MAX_HISTORY_MESSAGES`.

## Technical Details

### Streaming Implementation

The implementation uses AI job initialization and polling of job's already received chunks and status.

### Session Management

Each browser session gets a unique conversation ID stored in the HTTP session. This allows multiple tabs to maintain separate conversations.

### Security

- All endpoints require admin authentication (via Spring Security)
- CSP nonces are used for inline scripts and styles
- API key is never exposed to the client
- Input is sanitized before display

### Error Handling

- Missing API key: User-friendly error message displayed
- Network errors: Automatic retry with exponential backoff
- OpenAI API errors: Error messages displayed in chat interface
- Streaming interruptions: Graceful degradation

## Future Enhancements

Potential improvements for future versions:

1. **Web Search Tool**: Enable the agent to search the web for information (requires OpenAI Responses API)
2. **File Upload**: Allow uploading reference documents
3. **Multi-language Support**: Support for other languages besides Czech
4. **Custom Tools**: Integrate custom tools like image generation, SEO analysis, etc.
