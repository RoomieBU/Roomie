
import roomieLogo from "../assets/roomie-favicon.svg"
import "./RoommateChat.css"
import { useEffect, useState, useCallback } from "react";

function RoommateChat() {

    const [groupchat, setGroupchat] = useState()
    const [userEmail, setUserEmail] = useState("")
    const [messages, setMessages] = useState([])
    const [chatHistory, setChatHistory] = useState([])
    const [groupchatMembers, setGroupchatMembers] = useState([])
    const [chatInformation, setChatInformation] = useState([])


    // get user email
    const fetchUserEmail = async () => {
        try {
            const response = await fetch("https://roomie.ddns.net:8080/profile/getUserEmail", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ token: localStorage.getItem("token") })
            });

            if (!response.ok) throw new Error("Failed to fetch user email");
            const result = await response.json();
            console.log("EMAIL", result)
            setUserEmail(result.email);
        } catch (error) {
            console.error("Error fetching user email: ", error);
        }
    };

    // get groupchat id
    const checkIfConfirmed = async () => {
        const response = await fetch("https://roomie.ddns.net:8080/matches/isUserCurrentRoommate", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ token: localStorage.getItem("token") }),
        });

        if (response.ok) {
            const data = await response.json();
            setGroupchat(data.groupchatid);
        }
    };

    useEffect(() => {
        fetchUserEmail()
        checkIfConfirmed();
    }, []);

    // send message to database
    

    // get chatHistory
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

    // get groupchat user information --> all users in the groupchatID
    // first name, last name, profile picture
    const getAllUserInformation = useCallback(async () => {
        try {
            const response = await fetch("https://roomie.ddns.net:8080/chat/getAllUserInformation", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ groupchat_id: groupchat,
                    token: localStorage.getItem("token")
                 })
            });
            
            if (!response.ok) throw new Error("Failed to fetch all user information");
            console.log(response)

            setGroupchatMembers(response);
        } catch (error) {
            console.error("Error fetching all user information", error);
            return null;
        }
    }, [groupchat]);


    useEffect(() => {
        if (groupchat) {
            const fetchChatHistory = async () => {
                const history = await getChatHistory(groupchat)
                console.log(history)
                setChatHistory(history)
            }
            fetchChatHistory()
            getAllUserInformation()
        }
    }, [groupchat, getAllUserInformation])

    

    // const getChatInformation = async () => {

    //     const Chat = function (firstName, lastName, groupChatId, profilePicture) {
    //         this.firstName = firstName;
    //         this.lastName = lastName;
    //         this.groupChatId = groupChatId;
    //         this.profilePicture = profilePicture;
    //     };
        

    //     try {
    //         const response = await fetch("https://roomie.ddns.net:8080/matches/getChatInformation", {
    //             method: "POST",
    //             headers: { "Content-Type": "application/json" },
    //             body: JSON.stringify({ email })
    //         });

    //         if (!response.ok) throw new Error("Failed to fetch chat info");

    //         const chatInfo = await response.json();
    //         groupchatMembers.push(new Chat(
    //             chatInfo.first_name,
    //             chatInfo.last_name,
    //             groupchat,
    //             chatInfo.profile_picture_url
    //         ));
            
    //     } catch (error) {
    //         console.error("Error fetching chat information", error);
    //         return null;
    //     }

    //     setGroupchatMembers(groupchatMembers)
    // }




    return (
        <>
            <header className="dashboard-header">
                <div className="logo">
                    <a href="/"><img src={roomieLogo} alt="Roomie Logo" /></a>
                </div>
                <div className="nav-links">
                    <a href="/Dashboard">EDIT PROFILE</a>
                    <a href="/">SIGN OUT.</a>
                </div>
            </header>

            <div className="chat-page-container">
                <div className="chat-wrapper">
                    <div className="chat-header">
                        <div className="roommate-picture">JL</div >
                        <div className="roommate-picture">JD</div >
                        <div className="roommate-picture">SK</div >
                    </div>
                    <div className="chat-area">

                        {chatHistory.map((msg, index) => (
                            <div
                            key={index}
                            className={`bubble ${msg.sentBySelf ? "right" : "left"}`}
                            >
                            <label>{msg.senderEmail}</label>
                            <div className="message-text">{msg.message}</div>
                            </div>
                        ))}
                    </div>
                    <div className="message-input">
                        <textarea
                            // value={text}
                            // onChange={(e) => setText(e.target.value)}
                            // onKeyDown={handleKeyPress}
                            onInput={(e) => {
                                e.target.style.height = "50px"; // Reset height to auto to recalculate
                                e.target.style.height = `${e.target.scrollHeight}px`; // Set new height
                            }}
                            style={{ resize: "none", overflowY: "hidden" }} // Prevent manual resizing
                            placeholder="Type a message..."
                            className="messageTextBox form-control"
                        />
                        {/* onClick={sendMessage} */}
                        <button  className="chatButton">
                            <i className="bi bi-send" />
                        </button>
                    </div>
                </div>
            </div>
        </>
    )
}

export default RoommateChat