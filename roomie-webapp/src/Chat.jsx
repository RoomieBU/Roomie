import "./Chat.css"
import { useState, useEffect, useRef } from "react"
import PropTypes from 'prop-types';

function Chat({ selectedChat }) {
    const [text, setText] = useState('')
    const [messages, setMessages] = useState([])
    const [chatHistory, setChatHistory] = useState([])
    const [requestStatus, setRequestStatus] = useState("")

    const messageAreaRef = useRef(null)

    const [isAlertModalOpen, setIsAlertModalOpen] = useState(false);
    const [alertForm, setAlertForm] = useState({
        name: "",
        description: "",
        start_time: "",
        end_time: ""
    });


    let name = selectedChat ? `${selectedChat[0]} ${selectedChat[1]}` : "...";

    useEffect(() => {
        setMessages([])
        if (selectedChat) {
            const fetchChatHistory = async () => {
                const history = await getChatHistory(selectedChat[2])
                setChatHistory(history)
            }
            fetchChatHistory()

            const fetchRequestStatus = async () => {
                await getRoommateRequestStatus()
            }
            fetchRequestStatus()

        }
    }, [selectedChat])

    useEffect(() => {
        if (chatHistory.length > 0) {
            const sortedHistory = [...chatHistory].sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp));
            setMessages(sortedHistory); // Updating messages with sorted history
            console.log("THIS IS IT", sortedHistory)
        }
    }, [chatHistory]);

    useEffect(() => {
        scrollToBottom()
    }, [messages])

    function sendMessage() {
        if (!selectedChat) return

        if (text.length === 0) return

        const newMessage = {
            sentBySelf: true,
            message: text
        }

        setMessages((prevMessages) => [...prevMessages, newMessage])

        const sendMessageData = async () => {
            try {
                const messageData = JSON.stringify({
                    token: localStorage.getItem("token"),
                    groupchat_id: selectedChat[2],
                    message: encodeURIComponent(text)
                })

                const response = await fetch("https://roomie.ddns.net:8080/chat/sendMessage", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: messageData,
                });

                if (!response.ok) {
                    throw new Error("Message Data Sending failed. Please try again.");
                }

            } catch (error) {
                console.error("HERE we are", error)
            }
        }
        sendMessageData()


        setText("")
    }

    function handleKeyPress(e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault(); // Prevents default behavior (new line)
            sendMessage(e);
            e.target.style.height = "50px"
        }
    }

    const scrollToBottom = () => {
        if (messageAreaRef.current) {
            messageAreaRef.current.scrollTop = messageAreaRef.current.scrollHeight
        }
    }

    const getChatHistory = async (id) => {
        try {
            const response = await fetch("https://roomie.ddns.net:8080/chat/getChatHistory", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ token: localStorage.getItem("token"), groupchat_id: id })
            });

            if (!response.ok) {
                throw new Error("Failed to fetch chat history");
            }

            const result = await response.json();
            return result

        } catch (error) {
            console.error("Error fetching chat history: ", error)
        }
    }


    // Check Roommate Request Status
    const getRoommateRequestStatus = async () => {
        // check if there is an active request in this chat and who sent it
        const statusData = JSON.stringify({
            token: localStorage.getItem("token"),
            groupchat_id: selectedChat[2],
        })


        try {
            const response = await fetch("https://roomie.ddns.net:8080/chat/getRoommateRequestStatus", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: statusData,
            });

            if (!response.ok) {
                throw new Error("Failed to fetch roommate request status");
            }

            const result = await response.json();
            setRequestStatus(result.status)

        } catch (error) {
            console.error("Error fetching roommate request status: ", error)
        }
    }

    // Reset Roommate Request
    const resetRequestChoice = async () => {
        console.log("Resetting roommate request...")

        try {
            const data = JSON.stringify({
                token: localStorage.getItem("token"),
                groupchat_id: selectedChat[2],
            })

            const response = await fetch("https://roomie.ddns.net:8080/chat/resetRoommateRequestChoice", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: data,
            });

            if (!response.ok) {
                throw new Error("Roommate request reset failed. Please try again.");
            }

        } catch (error) {
            console.error("Error calling resetting roommate request", error)
        }

    }

    // handle request choice
    async function handleRequestChoice(choice) {
        closeModal();

        await requestRoommate(choice);      // Wait for the request to finish
        await getRoommateRequestStatus();   // Then call this
    }

    const submitAlert = async () => {
        try {
            const data = JSON.stringify({
                token: localStorage.getItem("token"),
                name: alertForm.name,
                description: alertForm.description,
                groupchat_id: selectedChat[2],
                start_time: alertForm.start_time,
                end_time: alertForm.end_time
            });

            const response = await fetch("https://roomie.ddns.net:8080/alert/addAlert", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: data
            });

            if (!response.ok) {
                throw new Error("Failed to submit alert");
            }

            alert("Alert created!");
            setIsAlertModalOpen(false);
            setAlertForm({ name: "", description: "", start_time: "", end_time: "" });

        } catch (error) {
            console.error("Error submitting alert:", error);
        }
    }



    // Request Roommate
    const requestRoommate = async (choice) => {

        console.log("Requesting Roommate...")

        try {
            const roommateRequest = JSON.stringify({
                token: localStorage.getItem("token"),
                groupchat_id: selectedChat[2],
                response: choice
            })

            const response = await fetch("https://roomie.ddns.net:8080/chat/requestRoommate", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: roommateRequest,
            });

            if (!response.ok) {
                throw new Error("Roommate request failed. Please try again.");
            }

        } catch (error) {
            console.error("Error calling roommate request", error)
        }
    }

    // handle changing response
    function handleChangeMind() {
        resetRequestChoice()
        setRequestStatus("No Request Yet")
    }

    // State to track if the modal is open
    const [isModalOpen, setIsModalOpen] = useState(false);

    // Function to open the modal
    const openModal = () => {
        setIsModalOpen(true);
    };

    // Function to close the modal
    const closeModal = () => {
        setIsModalOpen(false);
    };


    return (
        <div className="holdChat">
            <h5 className="chatNote" >You are chatting with {name}</h5>
            <div className="messageArea" ref={messageAreaRef}>  
                {messages.map((msg, index) => (
                    msg.sentBySelf === true ? (
                        <p key={index} className="right bubble"> {decodeURIComponent(msg.message)}</p>
                    ) : (
                        <p key={index} className="left bubble"> {decodeURIComponent(msg.message)}</p>
                    )
                ))}

            </div>
            <div className="messageInput">

                <button onClick={openModal} className="chatButton">
                    <i className="bi bi-hand-thumbs-up" />
                </button>

                <button onClick={() => setIsAlertModalOpen(true)} className="chatButton">
                    <i className="bi bi-exclamation-triangle" />
                </button>

                {isAlertModalOpen && (
                    <div className="modal-overlay">
                        <div className="modal-content">
                            <div className="holdCloseAndH2">
                                <h2>Create an Alert</h2>
                                <span className="close-button" onClick={() => setIsAlertModalOpen(false)}>&times;</span>
                            </div>
                            <input
                                type="text"
                                placeholder="Alert Name"
                                className="form-control mb-2"
                                value={alertForm.name}
                                onChange={(e) => setAlertForm({ ...alertForm, name: e.target.value })}
                            />
                            <textarea
                                placeholder="Description"
                                className="form-control mb-2"
                                value={alertForm.description}
                                onChange={(e) => setAlertForm({ ...alertForm, description: e.target.value })}
                            />
                            <input
                                type="datetime-local"
                                className="form-control mb-2"
                                value={alertForm.start_time}
                                onChange={(e) => setAlertForm({ ...alertForm, start_time: e.target.value })}
                            />
                            <input
                                type="datetime-local"
                                className="form-control mb-2"
                                value={alertForm.end_time}
                                onChange={(e) => setAlertForm({ ...alertForm, end_time: e.target.value })}
                            />
                            <button onClick={submitAlert} className="chatButton yesButton">Submit Alert</button>
                        </div>
                    </div>
                )}


                {/* Modal with full-screen overlay */}
                {isModalOpen && (
                    <div className="modal-overlay">
                        {requestStatus === "No Request Yet" && (
                            <div className="modal-content">
                                <div className="holdCloseAndH2">
                                    <h2>Would you like to be Roomies?</h2>
                                    <span className="close-button" onClick={closeModal}>&times;</span>
                                </div>
                                <div className="button-cluster">
                                    <button onClick={() => handleRequestChoice(0)} className="chatButton noButton">No</button>
                                    <button onClick={() => handleRequestChoice(1)} className="chatButton yesButton">Yes</button>
                                </div>
                            </div>
                        )}

                    {requestStatus === "Pending" && (
                        <div className="modal-content">
                            <div className="holdCloseAndH2">
                                <h2>Pending Status</h2>
                                <span className="close-button" onClick={closeModal}>&times;</span>
                            </div>
                        </div>
                    )}
            
                    {requestStatus === "Accepted" && (
                        <div className="modal-content">
                            <div className="holdCloseAndH2">
                                <h2>You have requested to be Roomies!</h2>
                                <span className="close-button" onClick={closeModal}>&times;</span>
                            </div>
                            <p className="changeMind" onClick={handleChangeMind}>Change your mind?</p>
                        </div>
                    )}
            
                    {requestStatus === "Declined" && (
                        <div className="modal-content">
                            <div className="holdCloseAndH2">
                                <h2>You have declined becoming Roomies.</h2>
                                <span className="close-button" onClick={closeModal}>&times;</span>
                            </div>
                            <p className="changeMind" onClick={handleChangeMind}>Change your mind?</p>
                        </div>
                    )}
                </div>
                )}

                <textarea
                    value={text}
                    onChange={(e) => setText(e.target.value)}
                    onKeyDown={handleKeyPress}
                    onInput={(e) => {
                        e.target.style.height = "50px"; // Reset height to auto to recalculate
                        e.target.style.height = `${e.target.scrollHeight}px`; // Set new height
                    }}
                    style={{ resize: "none", overflowY: "hidden" }} // Prevent manual resizing
                    placeholder="Type a message..."
                    className="messageTextBox form-control"
                />
                {/* <textarea value={text} onChange={(e) => setText(e.target.value)} onKeyDown={handleKeyPress} style={{resize: "none"}} className="form-control" type="text"/> */}
                <button onClick={sendMessage} className="chatButton">
                    <i className="bi bi-send" />
                </button>
            </div>

        </div>
    )

}
Chat.propTypes = {
    selectedChat: PropTypes.object
};

export default Chat                                        