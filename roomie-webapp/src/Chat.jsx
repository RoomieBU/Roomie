import "./Chat.css"
import { useState, useEffect, useRef } from "react"
import PropTypes from 'prop-types';

function Chat({ selectedChat }) {
    const [text, setText] = useState('')
    const [conversation, setConversation] = useState([])

    const messageAreaRef = useRef(null)

    let name = selectedChat && selectedChat.name ? selectedChat.name : "Select a contact";

    useEffect(() => {
        // reset chat for new selected chat here --> for now just clear conversation area
        setConversation([])

    }, [selectedChat])

    useEffect(() => {
        scrollToBottom()
    }, [conversation])

    function sendMessage() {
        // send message to db and to matched roomate...
        if (text.trim() !== '') {
          
            const newMessage = {
                type: 'message',
                content: text,
                timestamp: new Date().getTime()
            }

            const newResponse = {
                type: 'response',
                content: "You are so right",
                timestamp: new Date().getTime() + 1
            }

            // Add both to conversation array --> will be different when chat implemented
            setConversation([...conversation, newMessage, newResponse])
            
            // Clear the input field after sending
            setText('');
          }
    }

    function handleKeyPress(e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault(); // Prevents default behavior (new line)
            sendMessage();
        }
    }

    const scrollToBottom = () => {
        if (messageAreaRef.current) {
            messageAreaRef.current.scrollTop = messageAreaRef.current.scrollHeight
        }
    }

    return (
        <div className="holdChat">
            <div className="messageArea" ref={messageAreaRef}>
                <h5 className="chatNote" >You are chatting with {name}</h5>
                {conversation.map((item, index) => (
                    item.type === 'message' ? (
                        <p key={index} className="messageBubble">{item.content}</p>
                    ) : (
                        <p key={index} className="responseBubble">{item.content}</p>
                    )
                ))}
            </div>
            <div className="messageInput">
                <textarea value={text} onChange={(e) => setText(e.target.value)} onKeyDown={handleKeyPress} style={{resize: "none"}} className="form-control" type="text"/>
                <button onClick={sendMessage} className="btn btn-primary sendButton">Send</button>
            </div>
            
        </div>
    )

}
Chat.propTypes = {
    selectedChat: PropTypes.object
};

export default Chat                                        