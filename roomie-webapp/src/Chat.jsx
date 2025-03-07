import "./Chat.css"
import { useState } from "react"

function Chat() {
    const [text, setText] = useState('')
    const [messages, setMessages] = useState([]);
    const [responses, setResponses] = useState([]);

    function sendMessage() {
        // send message to db and to matched roomate...
        if (text.trim() !== '') {
            // Add the current text to messages array
            setMessages([...messages, text]);

            // Testing bubble look
            setResponses([...responses, "This is a response"])
            // Clear the input field after sending
            setText('');
          }
    }

    return (
        <div className="holdChat">
            <div className="messageArea">
                {/* Map through all messages and render them as <p> elements */}
                {messages.map((message, index) => (
                    <p key={index} className="messageBubble">{message}</p>
                ))}
                {responses.map((response, index) => (
                    <p key={index} className="responseBubble">{response}</p>    
                ))}
            </div>
            <div className="messageInput">
                <textarea value={text} onChange={(e) => setText(e.target.value)} style={{resize: "none"}} className="form-control" type="text"/>
                <button onClick={sendMessage} className="btn btn-primary sendButton">Send</button>
            </div>
            
        </div>
    )

}

export default Chat                                             